#!/usr/bin/python3
import re
import os
from spellchecker import SpellChecker

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

    # remove all links
    msg_clean = re.sub(r"https[^\s\"]*", "", msg_clean)

    # for csv format, strings are quoted, and " is replaced with ""
    # we don't need these, so single quotes are removed and "" are turned into "
    msg_clean = re.sub(r"\"([^\"])", r"\1", msg_clean)
    msg_clean = re.sub(r"\"\"", "\"", msg_clean)

    # remove extraneous spaces
    msg_clean = re.sub(r"[\s]{2,}", " ", msg_clean)

    # TODO fix common misspellings
    
    emotes = open(os.path.join(os.getcwd(), "data/emotes.csv"), "r")
    emote_list_csv = list(emotes)
    emote_list = []
    for line in emote_list_csv:
        emote_list.append(line.split(",")[0])
    spell = SpellChecker()
    spell.word_frequency.load_words(emote_list) # enter list of emotes here

    word_list = msg_clean.split()

    new_msg_list = []
    for word in word_list:
        if (word != '\U000e0000'):
            print(word)
            new_msg_list.append(spell.correction(word))
    msg_clean = ' '.join(new_msg_list)

    # stem exaggerations
    for reg, rep in common_exags:
        msg_clean = re.sub(reg, rep, msg_clean)

    # TODO other things idk


    return msg_meta + " " + msg_clean + "\n"


def main():
    raw_data = open(os.path.join(os.getcwd(), "data/test.csv"), "r")
    clean_data = open(os.path.join(os.getcwd(), "data/clean.csv"), "w")

    lines = list(raw_data)
    num_lines = len(lines)
    for i in range(147):
        clean_data.write(clean(lines[i]))

        # DONT UNCOMMENT these mofos slow this process down an ungodly amount
        #os.system('clear')
        #print(i + 1, "/", num_lines, "cleaned")


main()