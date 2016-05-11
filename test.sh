
# Take command line argument as name of problem, and compile
arg=$1
lc="${1,,}"
javac ./${arg}/${arg}.java -d .

# Test each test case
for file in ./testCases/${lc}/*.in; do
    fname="$(basename ${file%.*})"
    echo ${fname}
    java ${arg} < ${file} > ./testCases/${lc}/${fname}.out
    diff ./testCases/${lc}/${fname}.out ./testCases/${lc}/${fname}.ans
done

# Clean up
rm ./testCases/${lc}/*.out ./*.class
echo "Done."
