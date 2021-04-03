import csv
import sys
import matplotlib.pyplot as plt


if __name__ == "__main__":
    filename = sys.argv[1]
    print(f"filename is {filename}")
    lvts = []
    checkpoints = []
    with open(filename) as csvfile:
        reader = csv.reader(csvfile)
        for row in reader:
            lvts.append(int(row[0]))
            checkpoints.append(int(row[1]))
        print(lvts)
        print(checkpoints)
    plt.plot(checkpoints)
    # plt.plot(lvts)
    plt.show()
