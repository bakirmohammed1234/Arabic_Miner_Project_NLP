#  Text Mining Project V2 – Arabic Text Processing 

## Project Description

This project allows **cleaning, normalizing, stemming, and analyzing Arabic texts**. It is designed to process multiple `.txt` files and calculate **TF-IDF statistics** on the roots of words.

The full pipeline includes:  
1. Cleaning and normalization of Arabic texts  
2. Stopword removal  
3. Automatic stemming using a Python script with `NLTK’s ISRIStemmer`
4. Calculating word frequency, TF, IDF, and TF-IDF for each root  
5. Displaying a **final global result** including the root distribution  

---


---

##  Prerequisites

- **Java 11+**  
- **Python 3+**  
- Python libraries:  
  - `NLTK’s ISRIStemmer` 
- Arabic stopwords file  

---

##  How to Run

1. **Compile And Run Java code:**

```bash
javac -encoding UTF-8 -cp  *.java
java  TextProcessor ../data/ ../data1/stopwords.txt
```
2. ** Expected output:**
   
   <img width="858" height="263" alt="image" src="https://github.com/user-attachments/assets/eadad467-c80e-434c-aafc-f79bb545f050" />
------------------------------------------------------------------
   <img width="1014" height="429" alt="image" src="https://github.com/user-attachments/assets/3669d627-4efd-461a-aa47-0c236d5b7281" />
   




