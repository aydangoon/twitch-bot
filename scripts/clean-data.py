#!/usr/bin/python3
import re
import os

common_exags = [
    # words
    (r"(l|L)[lL]*[oO]+[lL]+([oO]?)+", "lol" if r"\1" == "l"  else "LOL"),
    (r"(l|L)[lL]*[uU]+[lL]+([uU]?)+", "lul" if r"\1" == "l"  else "LUL"),
    # symbols
    (r"!{4,}", "!!!"),
    (r"\?{4,}", "???"),
    # single letters, lmaooooooo -> lmao
    (r"(.)\1{2,}", r"\1\1\1"),
]
def clean(line):

    comma_cnt = 0
    for i in range(len(line)):
       if line[i] == ',':
            comma_cnt+=1
            if comma_cnt == 5:
                split_ind = i
    msg_meta = line[:split_ind + 1]
    msg_clean = line[split_ind + 1:]

    # remove extraneous spaces
    msg_clean = re.sub(r"[\s]{2,}", " ", msg_clean)

    # remove all links
    msg_clean = re.sub(r"https[^\s\"]*", "", msg_clean)

    # TODO fix common misspellings

    # stem exaggerations
    for reg, rep in common_exags:
        msg_clean = re.sub(reg, rep, msg_clean)

    # TODO other things idk


    return msg_meta + " " + msg_clean


def main():
    raw_data = open(os.path.join(os.getcwd(), "data/test.csv"), "r")
    clean_data = open(os.path.join(os.getcwd(), "data/clean.csv"), "w")

    for line in raw_data:
        clean_data.write(clean(line))


main()