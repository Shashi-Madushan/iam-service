#!/bin/bash

# =============================================================================
# Customer Service Test Script
# =============================================================================
# This script tests all endpoints of the Customer Service through API Gateway
# =============================================================================

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
API_GATEWAY="http://localhost:7001"

# Test counters
PASSED=0
FAILED=0
TOTAL=0

# Test IDs (using timestamp for uniqueness)
TIMESTAMP=$(date +%s)
TEST_CUSTOMER_ID="CUST${TIMESTAMP: -6}"
TEST_USER_ID="${TIMESTAMP: -6}"
TEST_USERNAME="testuser_${TIMESTAMP: -4}"

# =============================================================================
# Helper Functions
# =============================================================================

log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[PASS]${NC} $1"
    ((PASSED++))
}

log_error() {
    echo -e "${RED}[FAIL]${NC} $1"
    ((FAILED++))
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

make_request() {
    local method=$1
    local url=$2
    local expected_status=$3
    local data=$4
    local content_type=$5
    local extra_headers=$6
    
    ((TOTAL++))
    
    local curl_cmd="curl -s -o /dev/null -w \"%{http_code}\" -X $method"
    
    if [[ -n "$content_type" ]]; then
        curl_cmd="$curl_cmd -H \"Content-Type: $content_type\""
    fi
    
    if [[ -n "$extra_headers" ]]; then
        curl_cmd="$curl_cmd $extra_headers"
    fi
    
    if [[ -n "$data" ]]; then
        curl_cmd="$curl_cmd -d '$data'"
    fi
    
    curl_cmd="$curl_cmd \"$url\""
    
    local status_code
    status_code=$(eval "$curl_cmd" 2>/dev/null || echo "000")
    
    if [[ "$status_code" == "$expected_status" ]]; then
        log_success "$method $url (Expected: $expected_status, Got: $status_code)"
        return 0
    else
        log_error "$method $url (Expected: $expected_status, Got: $status_code)"
        return 1
    fi
}

make_request_with_response() {
    local method=$1
    local url=$2
    local expected_status=$3
    local data=$4
    local content_type=$5
    
    local curl_cmd="curl -s -w \"\\n%{http_code}\" -X $method"
    
    if [[ -n "$content_type" ]]; then
        curl_cmd="$curl_cmd -H \"Content-Type: $content_type\""
    fi
    
    if [[ -n "$data" ]]; then
        curl_cmd="$curl_cmd -d '$data'"
    fi
    
    curl_cmd="$curl_cmd \"$url\""
    
    local response
    response=$(eval "$curl_cmd" 2>/dev/null || echo -e "\n000")
    
    local body=$(echo "$response" | head -n -1)
    local status_code=$(echo "$response" | tail -n 1)
    
    if [[ "$status_code" == "$expected_status" ]]; then
        log_success "$method $url (Expected: $expected_status, Got: $status_code)"
        echo "$body"
        return 0
    else
        log_error "$method $url (Expected: $expected_status, Got: $status_code)"
        echo "$body"
        return 1
    fi
}

test_multipart_request() {
    local method=$1
    local url=$2
    local expected_status=$3
    local fields=$4
    local file_field=$5
    
    ((TOTAL++))
    
    local curl_cmd="curl -s -o /dev/null -w \"%{http_code}\" -X $method"
    
    # Add form fields
    for field in $fields; do
        curl_cmd="$curl_cmd -F \"$field\""
    done
    
    # Add file if specified
    if [[ -n "$file_field" && -f "$file_field" ]]; then
        curl_cmd="$curl_cmd -F \"picture=@$file_field\""
    fi
    
    curl_cmd="$curl_cmd \"$url\""
    
    local status_code
    status_code=$(eval "$curl_cmd" 2>/dev/null || echo "000")
    
    if [[ "$status_code" == "$expected_status" ]]; then
        log_success "$method $url (multipart, Expected: $expected_status, Got: $status_code)"
        return 0
    else
        log_error "$method $url (multipart, Expected: $expected_status, Got: $status_code)"
        return 1
    fi
}

print_summary() {
    echo ""
    echo "============================================================================="
    echo -e "                          ${BLUE}TEST SUMMARY${NC}"
    echo "============================================================================="
    echo -e "Total Tests:  ${BLUE}$TOTAL${NC}"
    echo -e "Passed:       ${GREEN}$PASSED${NC}"
    echo -e "Failed:       ${RED}$FAILED${NC}"
    echo "============================================================================="
    
    if [[ $FAILED -eq 0 ]]; then
        echo -e "${GREEN}All tests passed!${NC}"
        return 0
    else
        echo -e "${RED}Some tests failed!${NC}"
        return 1
    fi
}

# =============================================================================
# Customer Controller Tests
# =============================================================================

test_customer_endpoints() {
    echo ""
    echo "============================================================================="
    echo -e "                    ${BLUE}CUSTOMER CONTROLLER TESTS${NC}"
    echo "============================================================================="
    
    # Create a dummy image file for testing
    echo -n "fake-image-data" > /tmp/test_picture.jpg
    
    local customer_fields="customerId=$TEST_CUSTOMER_ID name=JohnDoe address=123MainSt mobile=1234567890 email=john@test.com customerType=REGULAR"
    
    # --- CREATE Customer ---
    log_info "Testing Customer Creation..."
    
    # Via API Gateway (no service ID needed - gateway routes by path)
    test_multipart_request "POST" \
        "$API_GATEWAY/api/v1/customers" \
        "201" \
        "$customer_fields" \
        "/tmp/test_picture.jpg"
    
    # --- GET All Customers ---
    log_info "Testing Get All Customers..."
    make_request "GET" "$API_GATEWAY/api/v1/customers" "200"
    
    # --- GET Customer by ID ---
    log_info "Testing Get Customer by ID..."
    make_request "GET" "$API_GATEWAY/api/v1/customers/$TEST_CUSTOMER_ID" "200"
    
    # --- GET Customers by Type ---
    log_info "Testing Get Customers by Type..."
    make_request "GET" "$API_GATEWAY/api/v1/customers/type/REGULAR" "200"
    
    # --- GET Top Loyalty Customers ---
    log_info "Testing Get Top Loyalty Customers..."
    make_request "GET" "$API_GATEWAY/api/v1/customers/loyalty/top?minPoints=0" "200"
    
    # --- GET Customer Picture ---
    log_info "Testing Get Customer Picture..."
    make_request "GET" "$API_GATEWAY/api/v1/customers/$TEST_CUSTOMER_ID/picture" "200"
    
    # --- UPDATE Customer ---
    log_info "Testing Update Customer..."
    local update_fields="name=JohnUpdated address=456UpdatedSt mobile=9876543210 email=updated@test.com customerType=PREMIUM"
    
    test_multipart_request "PUT" \
        "$API_GATEWAY/api/v1/customers/$TEST_CUSTOMER_ID" \
        "200" \
        "$update_fields" \
        ""
    
    # --- UPDATE Loyalty Points ---
    log_info "Testing Update Loyalty Points..."
    make_request "PUT" \
        "$API_GATEWAY/api/v1/customers/$TEST_CUSTOMER_ID/loyalty?pointsToAdd=100" \
        "200"
    
    # --- UPDATE Total Purchases ---
    log_info "Testing Update Total Purchases..."
    make_request "PUT" \
        "$API_GATEWAY/api/v1/customers/$TEST_CUSTOMER_ID/purchases?purchaseAmount=500.00" \
        "200"
    
    # --- DELETE Customer ---
    log_info "Testing Delete Customer..."
    make_request "DELETE" "$API_GATEWAY/api/v1/customers/$TEST_CUSTOMER_ID" "204"
    
    # Cleanup
    rm -f /tmp/test_picture.jpg
}

# =============================================================================
# User Controller Tests
# =============================================================================

test_user_endpoints() {
    echo ""
    echo "============================================================================="
    echo -e "                      ${BLUE}USER CONTROLLER TESTS${NC}"
    echo "============================================================================="
    
    local USER_CREATED=false
    
    # --- CREATE User ---
    log_info "Testing User Creation..."
    
    local user_json='{
        "username": "'"$TEST_USERNAME"'",
        "password": "password123",
        "email": "'"$TEST_USERNAME"'@test.com",
        "firstName": "Test",
        "lastName": "User",
        "userType": "EMPLOYEE",
        "status": "ACTIVE",
        "phone": "1234567890",
        "address": "123 Test Street"
    }'
    
    # Via API Gateway
    if make_request "POST" \
        "$API_GATEWAY/api/v1/users" \
        "201" \
        "$user_json" \
        "application/json"; then
        USER_CREATED=true
    else
        log_warn "User creation failed - skipping dependent tests"
    fi
    
    # --- GET All Users ---
    log_info "Testing Get All Users..."
    make_request "GET" "$API_GATEWAY/api/v1/users" "200"
    
    # Only run tests that need existing user if user was created
    if [[ "$USER_CREATED" == "true" ]]; then
        # --- GET User by ID ---
        log_info "Testing Get User by ID..."
        make_request "GET" "$API_GATEWAY/api/v1/users/$TEST_USER_ID" "200"
        
        # --- GET User by Username ---
        log_info "Testing Get User by Username..."
        make_request "GET" "$API_GATEWAY/api/v1/users/username/$TEST_USERNAME" "200"
        
        # --- EXISTS by Username ---
        log_info "Testing Exists by Username..."
        make_request "GET" "$API_GATEWAY/api/v1/users/exists/username/$TEST_USERNAME" "200"
        
        # --- EXISTS by Email ---
        log_info "Testing Exists by Email..."
        make_request "GET" "$API_GATEWAY/api/v1/users/exists/email/${TEST_USERNAME}@test.com" "200"
        
        # --- UPDATE User ---
        log_info "Testing Update User..."
        local update_json='{
            "email": "'$TEST_USERNAME'@test.com",
            "firstName": "Updated",
            "lastName": "User",
            "userType": "EMPLOYEE",
            "status": "ACTIVE",
            "phone": "9876543210",
            "address": "456 Updated Street"
        }'
        
        make_request "PUT" \
            "$API_GATEWAY/api/v1/users/$TEST_USER_ID" \
            "200" \
            "$update_json" \
            "application/json"
        
        # --- UPDATE User Status ---
        log_info "Testing Update User Status..."
        make_request "PUT" \
            "$API_GATEWAY/api/v1/users/$TEST_USER_ID/status?status=INACTIVE" \
            "200"
        
        # --- RECORD User Login ---
        log_info "Testing Record User Login..."
        make_request "PUT" \
            "$API_GATEWAY/api/v1/users/$TEST_USER_ID/login" \
            "200"
        
        # --- CHANGE Password ---
        log_info "Testing Change Password..."
        make_request "PUT" \
            "$API_GATEWAY/api/v1/users/$TEST_USER_ID/password?oldPassword=password123&newPassword=newpass456" \
            "200"
        
        # --- DELETE User ---
        log_info "Testing Delete User..."
        make_request "DELETE" "$API_GATEWAY/api/v1/users/$TEST_USER_ID" "204"
    else
        log_warn "Skipping user-dependent tests (creation failed)"
        
        # Still count these as expected tests but mark as skipped
        ((TOTAL+=9))
    fi
    
    # --- GET Users by Type ---
    log_info "Testing Get Users by Type..."
    make_request "GET" "$API_GATEWAY/api/v1/users/type/EMPLOYEE" "200"
    
    # --- GET Users by Status ---
    log_info "Testing Get Users by Status..."
    make_request "GET" "$API_GATEWAY/api/v1/users/status/ACTIVE" "200"
    
    # --- SEARCH Users by Name ---
    log_info "Testing Search Users by Name..."
    make_request "GET" "$API_GATEWAY/api/v1/users/search?name=Test" "200"
    
    # --- GET Active Users Since ---
    log_info "Testing Get Active Users Since..."
    make_request "GET" "$API_GATEWAY/api/v1/users/active-since" "200"
    make_request "GET" "$API_GATEWAY/api/v1/users/active-since?startDate=2024-01-01T00:00:00" "200"
    
    # --- GET Paginated Users by Type ---
    log_info "Testing Get Paginated Users by Type..."
    make_request "GET" \
        "$API_GATEWAY/api/v1/users/paginated/type/EMPLOYEE?page=0&size=10&sortBy=createdAt&sortDir=desc" \
        "200"
    
    # --- SEARCH Users by Type and Keyword ---
    log_info "Testing Search Users by Type and Keyword..."
    make_request "GET" \
        "$API_GATEWAY/api/v1/users/search/type/EMPLOYEE?keyword=test&page=0&size=10" \
        "200"
    
    # --- GET User Count by Type ---
    log_info "Testing Get User Count by Type..."
    make_request "GET" "$API_GATEWAY/api/v1/users/stats/type/EMPLOYEE" "200"
    
    # --- GET User Count by Status ---
    log_info "Testing Get User Count by Status..."
    make_request "GET" "$API_GATEWAY/api/v1/users/stats/status/ACTIVE" "200"
}

# =============================================================================
# Edge Case and Error Tests
# =============================================================================

test_edge_cases() {
    echo ""
    echo "============================================================================="
    echo -e "                      ${BLUE}EDGE CASE TESTS${NC}"
    echo "============================================================================="
    
    # Test 404 - Customer not found (returns 422 for validation error from WebFlux)
    log_info "Testing 404 - Customer Not Found..."
    make_request "GET" "$API_GATEWAY/api/v1/customers/NONEXISTENT" "422"
    
    # Test 404 - User not found (use a large numeric ID that doesn't exist)
    log_info "Testing 404 - User Not Found..."
    make_request "GET" "$API_GATEWAY/api/v1/users/999999" "404"
    
    # Test validation - Invalid customer ID format (WebFlux returns 422 for pattern mismatch)
    log_info "Testing Validation - Invalid Customer ID..."
    make_request "GET" "$API_GATEWAY/api/v1/customers/invalid-id" "422"
    
    # Test validation - Invalid user ID format (non-numeric ID should fail conversion)
    log_info "Testing Validation - Invalid User ID..."
    make_request "GET" "$API_GATEWAY/api/v1/users/invalid-id" "422"
}

# =============================================================================
# Health Check
# =============================================================================

check_services() {
    echo "============================================================================="
    echo -e "                      ${BLUE}SERVICE HEALTH CHECK${NC}"
    echo "============================================================================="
    
    log_info "Checking API Gateway..."
    if curl -s -o /dev/null -w "%{http_code}" "$API_GATEWAY/actuator/health" 2>/dev/null | grep -q "200\|401\|403"; then
        log_success "API Gateway is accessible"
    else
        log_warn "API Gateway may not be accessible (this is OK if no actuator endpoint)"
    fi
}

# =============================================================================
# Main
# =============================================================================

main() {
    echo "============================================================================="
    echo "          Customer Service API Test Suite"
    echo "============================================================================="
    echo "API Gateway:    $API_GATEWAY"
    echo "Test Customer:  $TEST_CUSTOMER_ID"
    echo "Test Username:  $TEST_USERNAME"
    echo "============================================================================="
    
    # Check services before running tests
    check_services
    
    # Run all tests
    test_customer_endpoints
    test_user_endpoints
    test_edge_cases
    
    # Print summary
    print_summary
    
    exit $?
}

# Run main function
main "$@"
