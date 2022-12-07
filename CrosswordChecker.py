print()
print("Crossword Checker")
print("-------------")
print()

lettersIn = input("Enter Letters (separated by spaces): ")
letters = lettersIn.split()
print()

wordsIn = input("Enter Words (separated by spaces): ")
words = wordsIn.split()
print()

count = 0
print("Your Letters:")
for letter in letters:
    if len(letter) > 1:
        count += 1
        if count > 9:
            print()
            count = 0
        print("error", end=" ")
    else:
        count += 1
        if count > 9:
            print()
            count = 0
        print(letter, end=" ")
print()
print()

print("Your Words:")
for word in words:
    print(word)
print()

foundWords = []
for word in words:
    count = len(word)
    found = 0
    for character in word:
        for letter in letters:
            if character == letter:
                found += 1
    if count == found:
        foundWords += [word]

foundNumber = len(foundWords)
prizeAmount = 0
if foundNumber < 2:
    prizeAmount = 0
elif foundNumber == 2:
    prizeAmount = 3
elif foundNumber == 3:
    prizeAmount = 9
elif foundNumber == 4:
    prizeAmount = 15
elif foundNumber == 5:
    prizeAmount = 25
elif foundNumber == 6:
    prizeAmount = 50
elif foundNumber == 7:
    prizeAmount = 100
elif foundNumber == 8:
    prizeAmount = 5000
elif foundNumber == 9:
    prizeAmount = 10000
elif foundNumber == 10:
    prizeAmount = 25000
elif prizeAmount == 11:
    prizeAmount = 50000

print("Your Prize Amount: $", prizeAmount)
print("Words Found:", foundNumber)
print("Your Found Words:")
for word in foundWords:
    print(word)
print()
