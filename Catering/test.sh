# unzip all of the test cases from ICPC and rename to be testCases
for file in ../testCases/catering/*.in; do
#    echo ${file}
    fname="$(basename ${file%.*})"
    echo ${fname}
    javac Catering.java
    java Catering < ${file} > ../testCases/catering/${fname}.out
    diff ../testCases/catering/${fname}.out ../testCases/catering/${fname}.ans
done
echo "Done."
