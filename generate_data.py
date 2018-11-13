import string
import random
from random import randint
from time import gmtime, strftime

print "start at: " + strftime("%Y-%m-%d %H:%M:%S", gmtime())

titles=["staff",'senior staff', 'engineer', 'senior engineer', 'assistant engineer', 'technique leader', 'manager'] #7
depts=['customer service', 'development', 'finance', 'human resources', 'marketing', 'production', 'quality management', 'research', 'sales'] #9
genders=['M','F']
def single_word(size, chars=string.ascii_uppercase + string.digits):
    return ''.join(random.choice(chars) for _ in range(size))

def name_rand():
    return single_word(6) + ' ' +  single_word(6)

emp = open("emp.csv", "w")
dept = open("dept.csv", 'w')
tits = open("titles.csv",'w')
dept_emp = open('dept_emp.csv', 'w')
dept_man = open('dept_man.csv', 'w')

emp.write('emp_no;first_name;last_name;gender;titles;departments' + '\n')
dept.write('dept_no;dept_name;managers' + '\n')
tits.write('emp_no;title' + '\n')
dept_emp.write('emp_no;dept_no' + '\n')
dept_man.write('dept_no;emp_no' + '\n')

for i in range(1, 100000001):
    empline = str(i) + ';' + single_word(6) + ';' + single_word(6) + ';' + genders[randint(0,1)] + ';'
    tit1 = randint(0,6)
    tit2 = randint(0,6)
    while (tit1 == tit2):
        tit2 = randint(0,6)

    tit = '"' + titles[tit1] + '"' + ',' + '"' + titles[tit2] + '"'
    empline += '[' + tit + ']' + ';'
    dept1 = randint(1,9) 
    dept2 = randint(1,9)
    while dept1 == dept2:
        dept2 = randint(1,9)

    de = '"' + 'd' + str(dept1) + '"' + ',' + '"' + 'd' + str(dept2) + '"'
    empline += '[' + de + ']' + '\n'
    emp.write(empline)
    tits.write(str(i) + ';' + titles[tit1] + '\n')
    tits.write(str(i) + ';' + titles[tit2] + '\n')
    dept_emp.write(str(i) + ';' + 'd' + str(dept1) +'\n')
    dept_emp.write(str(i) + ';' + 'd' + str(dept2) + '\n')
    
for i in range(9):
    deptline = 'd' + str(i + 1) + ';' + depts[i] + ';'
    man1 = randint(1, 100000000)
    man2 = randint(1, 100000000)
    while man1 == man2:
        man2 = randint(1, 100000000)
    man = '[' + str(man1) + ',' + str(man2) + ']'
    deptline += man + '\n'
    dept.write(deptline)
    dept_man.write('d' + str(i + 1) + ';' + str(man1) + '\n')
    dept_man.write('d' + str(i + 1) + ';' + str(man2) + '\n')

emp.close()
dept.close()
tits.close()
dept_emp.close()
dept_man.close()

print "end at: " + strftime("%Y-%m-%d %H:%M:%S", gmtime())
    
