buffer = "b" * 1048575
buffer += "aa"
buffer += "b" * 1048575
with open('2MB.txt', "w") as fin:
    fin.write(buffer)
    fin.flush()
with open('16GB.txt', "w") as fin:
    fin.write(buffer)
    fin.flush()
    for _ in range(8190):
        fin.write("b" * 1048576 * 2)
        fin.flush()
    fin.write(buffer)
    fin.flush()