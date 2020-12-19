# STEP 0: initialize everything
# note I put '.0' behind every whole number to make sure
# Python interpreter understands that I want floating-point
# precision (the x array are all whole numbers)
x = [2.0, 3.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0]  # dataset input
y = [2.0, 4.0, 3.0, 4.5, 6.0, 5.0, 7.6, 6.0]  # dataset output

xAverage = 0  # store average of all input values
yAverage = 0  # store average of all output values

sum1 = 0  # store STEP 3 summation #1
sum2 = 0  # store STEP 3 summation #2

# STEP 1
for i in x:
    xAverage += i  # summation of all values in x
for i in y:
    yAverage += i  # summation of all values in y

# STEP 2
xAverage /= len(x)  # divide by length of x array, finding average
yAverage /= len(y)  # divide by length of y array, finding average

# STEP 3
for i in range(len(x)):
    sum1 += (x[i] - xAverage) * (y[i] - yAverage)
    sum2 += (x[i] - xAverage) * (x[i] - xAverage)

# ...and divide it by sum2
slope = sum1 / sum2  # slope of line

# STEP 4
inter = yAverage - slope * xAverage  # find y-intercept

# print out values
print(sum1)
print(sum2)
print(slope)
print(inter)
print(xAverage)
print(yAverage)
