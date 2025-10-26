# 📄 Text Mining Project – Arabic Text Processing 

## Project Description

This project allows **cleaning, normalizing, stemming, and analyzing Arabic texts**. It is designed to process multiple `.txt` files and calculate **TF-IDF statistics** on the roots of words.

The full pipeline includes:  
1. Cleaning and normalization of Arabic texts  
2. Stopword removal  
3. Automatic stemming using a Python script with `NLTK’s ISRIStemmer` (or another Arabic stemmer)  
4. Calculating word frequency, TF, IDF, and TF-IDF for each root  
5. Displaying a **final global result** including the root distribution  

---


---

##  Prerequisites

- **Java 11+**  
- **Python 3+**  
- Java libraries:  
  - `lucene-core-8.11.4.jar`  
  - `lucene-analyzers-common-8.11.4.jar`  
  - `gson-2.10.1.jar`  
- Python libraries:  
  - `NLTK’s ISRIStemmer` 
- Arabic stopwords file (e.g., `stopwords.txt`)  

---

##  How to Run

1. **Compile And Run Java code:**

```bash
javac -encoding UTF-8 -cp  *.java
java  TextProcessor ../data/ ../data1/stopwords.txt
```
2. ** Expected output:**

   <img width="858" height="263" alt="image" src="https://github.com/user-attachments/assets/eadad467-c80e-434c-aafc-f79bb545f050" />




