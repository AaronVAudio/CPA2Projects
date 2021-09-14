import spacy
nlp = spacy.load('en')

inf = open("EmailLog.txt", "r", encoding='utf-8')

outf = open("ProjectOutput.txt", mode='wt', encoding='utf-8')

email = ''
amount = 0
receiver = ''
thisdict = {}
total = 0

for line in inf:
    if '@' in line:
        email = line[:-1]
    elif line != '<<End>>\n':
        doc = nlp(line)
        for token in doc:
            if token.tag_ == '$':
                # amount = token.text
                i = token.i+1
                while doc[i].tag_ == 'CD':
                    if doc[i].text == 'thousand':
                        amount *= 1000
                    elif doc[i].text == 'million':
                        amount *= 1000000
                    elif doc[i].text == 'hundred':
                        amount *= 100
                    else:
                        amount += int(doc[i].text.replace(',', ''))
                    i += 1
                while doc[i].pos_ != 'PROPN':
                    i += 1
                while doc[i].pos_ == 'PROPN':
                    receiver += doc[i].text + ' '
                    i += 1
                txt = '${:,}'
                amttxt = txt.format(amount)
                thisdict[amttxt] = receiver
                total += amount
                amount = 0
                receiver = ''
    elif line == '<<End>>\n':
        description = ''
        counter = 1
        for key, value in thisdict.items():
            if counter < len(thisdict) - 1 and len(thisdict) > 2:
                description += key + ' to ' + value[:-1] + ', '
                counter += 1
            elif counter == len(thisdict) - 1:
                description += key + ' to ' + value[:-1] + ' and '
                counter += 1
            else:
                description += key + ' to ' + value[:-1]
        outf.write(email + ' : ' + description + '\n')
        print(email, ':', description)
        thisdict = {}

txt = '${:,}'
tottxt = txt.format(total)
outf.write('\nTotal Requests: ' + tottxt)
print('\nTotal Requests:', tottxt)

inf.close()
outf.close()
