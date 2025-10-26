# -*- coding: utf-8 -*-
import sys
import json
from nltk.stem.isri import ISRIStemmer

stemmer = ISRIStemmer()

def main():
    data = sys.stdin.read()
    if not data:
        print("[]")
        return
    try:
        words = json.loads(data)
    except Exception:
        print("[]")
        return

    stems = []
    for w in words:
        if not w:
            stems.append(w)
            continue
        try:
            root = stemmer.stem(w)
        except Exception:
            root = w
        stems.append(root)
    print(json.dumps(stems, ensure_ascii=False))

if __name__ == "__main__":
    main()
