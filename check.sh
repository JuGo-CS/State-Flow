set -e

# Check if an argument was provided
if [ -n "$1" ]; then
    # If the argument doesn't already start with 'tests/', add it
    if [[ "$1" == tests/* ]]; then
        TARGET="$1"
    else
        TARGET="tests/$1"
    fi
    
    echo "Running tests for: $TARGET"
    python3 run_tests.py "$TARGET"
else
    # No arguments (./check) -> Loop through and test all lab folders
    echo "No target specified. Checking all test folders..."
    
    # Finds all directories matching 'tests/lab*' and loops through them
    for dir in tests/lab*/; do
        # Ensure the directory exists to prevent errors if the glob fails
        [ -d "$dir" ] || continue
        
        echo "========================================"
        echo "Testing: $dir"
        echo "========================================"
        
        # Run the Python test script on the current folder
        python3 run_tests.py "$dir"
    done
fi