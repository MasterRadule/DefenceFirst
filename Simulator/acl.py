import win32security
import os
import ntsecuritycon as con
import win32api


def show_cacls(filename):
    for line in os.popen("cacls %s" % filename).read().splitlines():
        print(line)


if __name__ == '__main__':
    help(con)
