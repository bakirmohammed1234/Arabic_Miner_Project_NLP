from nltk.stem.isri import ISRIStemmer
import os

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
input_path = os.path.join(BASE_DIR, "tmp", "cleaned.txt")
output_path = os.path.join(BASE_DIR, "tmp", "roots.txt")
stemmer = ISRIStemmer()

#print("hello bakir")
with open(input_path, encoding="utf-8") as fin, open(output_path, "w", encoding="utf-8") as fout:
    for line in fin:
        word = line.strip()
        if word:
            root = stemmer.stem(word)
            fout.write(root + "\n")

print(" Racinisation terminée. Fichier généré : tmp/roots.txt")
