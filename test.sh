# unzip all of the test cases from ICPC and rename to be testCases
arg=$1
lc="${1,,}"
javac ./${arg}/${arg}.java -d .
for file in ./testCases/${lc}/*.in; do
    fname="$(basename ${file%.*})"
    echo ${fname}
    java Catering < ${file} > ./testCases/${lc}/${fname}.out
    diff ./testCases/${lc}/${fname}.out ./testCases/${lc}/${fname}.ans
done
rm ./testCases/${lc}/*.out
rm ./*.class
echo "Done."
