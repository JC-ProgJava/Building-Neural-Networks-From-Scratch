x = [2.0, 3.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0]
y = [2.0, 4.0, 3.0, 4.5, 6.0, 5.0, 7.6, 6.0]

xAverage = 0
yAverage = 0

sum1 = 0
sum2 = 0

for i in x:
    xAverage += i
for i in y:
    yAverage += i

xAverage /= len(x)
yAverage /= len(y)

for i in range(len(x)):
    sum1 += (x[i] - xAverage) * (y[i] - yAverage)
    sum2 += (x[i] - xAverage) * (x[i] - xAverage)

slope = sum1 / sum2
inter = yAverage - slope * xAverage

print(sum1)
print(sum2)
print(slope)
print(inter)
print(xAverage)
print(yAverage)
