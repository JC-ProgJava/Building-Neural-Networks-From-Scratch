# Building-Neural-Networks-From-Scratch 

[![CodeFactor](https://www.codefactor.io/repository/github/jc-progjava/building-neural-networks-from-scratch/badge)](https://www.codefactor.io/repository/github/jc-progjava/building-neural-networks-from-scratch)
![LOC](https://tokei.rs/b1/github/JC-ProgJava/Building-Neural-Networks-From-Scratch)

Building Neural Networks From Scratch book repository.

You can start reading the book at https://jc-progjava.github.io/Building-Neural-Networks-From-Scratch/ or alternatively https://buildingneuralnetworks.netlify.app/.

A license for the code and book is provided at https://jc-progjava.github.io/Building-Neural-Networks-From-Scratch/preface.html#license.

Code can be found in this repository organized by chapter name/number.

Do provide feedback or tips & thanks through their respective forms! 🙂

Feedback: https://forms.gle/P68m2YurzxBecRLv6

Tips & Thanks: https://forms.gle/kEAdNtnJquAsuSGj7

## Paperback/Soft Offline Copy

For readers who want to be able to access this book offline or as a soft copy (PDF), download the book [here](https://github.com/JC-ProgJava/Building-Neural-Networks-From-Scratch/blob/main/Building%20Neural%20Networks%20from%20Scratch.pdf).

<br>

# Changelog
13 March 2022
- Take a peek into the result of the Optimizers chapter. In this chapter, you will be implmenting: `Momenutum`, `Adam`, `AdaGrad`, `AdaDelta`, and `RMSPROP` optimizers.
- Results comparison below:

| Configuration (784-32-10) | Testing Accuracy (%) | Configuration (784-128-10) Testing Accuracy (%) |
| -------------- | ------------ | -------------- | ------------ |
| Adam CCE LEAKYRELU-SOFTMAX LR0.001 | 96.41 |
| None CCE LEAKYRELU-SOFTMAX LR0.01 | 95.58 |
| Momentum CCE SIGMOID-SIGMOID LR0.01 | 95.18 |
| Adam CCE SIGMOID-SIGMOID LR0.001 | 94.60 |
| Momentum MSE SIGMOID-SIGMOID LR0.01 | 94.19 |
| Adam MSE SIGMOID-SIGMOID LR0.001 | 93.82 |
| AdaGrad MSE SIGMOID-SIGMOID LR0.01 | 93.30 |
| None CCE SIGMOID-SIGMOID LR0.01 | 92.21 |
| RMSProp CCE LEAKYRELU-SOFTMAX LR0.001 | 91.40 |
| Momentum CCE LEAKYRELU-SOFTMAX LR0.01 | 90.96 |
| AdaGrad CCE SIGMOID-SIGMOID LR0.01 | 90.91 |
| None MSE SIGMOID-SIGMOID LR0.01 | 88.01 |
| RMSProp MSE SIGMOID-SIGMOID LR0.001 | 36.67 (beginning to converge) |
| RMSProp CCE SIGMOID-SIGMOID LR0.001 | 30.45 (beginning to converge) |
| AdaDelta CCE SIGMOID-SIGMOID LR1 | 10.10 (Error) |
| AdaDelta CCE LEAKYRELU-SOFTMAX LR1 | Error |
| AdaDelta MSE SIGMOID-SIGMOID LR1 | Error |
| AdaGrad CCE LEAKYRELU-SOFTMAX LR0.01 | Error |

Info:
- All networks are
  - `784-32-10`
  - All weights initialized with 0.05 (avoid no learning).
  - Trained for 5 epochs with batch size 10
  - Use their optimal LRs (suggested by publications)
- `CCE`: Categorical Cross Entropy
- `MSE`: Mean Squared Error

Observations:
- Adam optimizer works well with a learning rate of 0.001. It also seems to work only with Cross Entropy.
- Sigmoid-Sigmoid configurations shouldn't be initialized with such small numbers (because the output is scaled to between 0 and 1, and too small inputs result in values close to 0 as output)
- LeakyRELU-Softmax does not work well with MSE
- Adam performs quite well.
- The networks were in general a bit small, they all ended up overfitting slightly (which is expected).

Take a look at the training process:

https://user-images.githubusercontent.com/61588096/158043895-428a6b22-aa19-4c77-9725-a746feb49907.mp4

12 March 2022
- Pushed code for Optimizers chapter 🚀

4 November 2021
- Taking a break and exploring Github Copilot.
- See it help program a driver class that trains a neural network! 🙂

https://user-images.githubusercontent.com/61588096/140303928-ef131a9d-8871-4dbe-b894-83fd28c84f45.mov

18 August 2021
- Pushed Chapter 8 - Backpropagation to web! 🚀

17 August 2021
- Fixed errata and finished Chapter 8 print version.

16 August 2021
- Added new chapter idea 'Generative Adversarial Networks'

15 August 2021
- 🚀 Dark mode!

12 August 2021
- Hit 30K words! 🙂

11 August 2021
- Finished ~40% of Chapter 8 - Backpropagation. Get ready for a push sometime in September!

10 August 2021
- Minor fixes and improvements.

https://user-images.githubusercontent.com/61588096/128802272-0052b94a-cf16-4a7f-9ce7-17b50c6a24c5.mov

- [Demos](https://jc-progjava.github.io/Building-Neural-Networks-From-Scratch/demos.html) webpage! 🚀

26 July 2021
- Fixed inconsistencies between print and web version.
- Renamed and added new chapter ideas to web. 🚀

24 July 2021

- Revised Chapter 3 - Neural Networks and Learning
  - Now includes extensive guide to the delta rule
  - More into derivatives (i.e., Chain Rule)
  - Step by step guide to deriving the delta rule.
- Renamed "Optimization" chapter to "Optimizers"
- Fixed some figure naming inconsistencies.
- Adjust spacing of ordered list in Data Preprocessing and Augmentation chapter.

19 July 2021

- Finished Chapter 7 Data Preprocessing & Augmentation. 🙂
- Pushed Chapter 7 to the web. 🚀
- Added "Data_Preprocessing_Augmentation" code folder to Github.

9 May 2021

- Combined chapters Data Augmentation (Chpt. 11) and Image Preprocessing (Chpt. 7) into Chpt. 7 (Image Preprocessing and Data Augmentation)
- Renamed chapter 7 to "Data Preprocessing & Augmentation"
- Created a separate version of the references list. It is more readable than the bibliography, and is for readers that are interested in how the book was made and want to read more on topics covered in the book.
- Started on Chapter 7 and opened issue #14.
- Added a page on fonts used in the book (Source Code Pro for code and Amiri for text. Both are under the Open Font License [OFL] and were obtained through Google Fonts)
- Updated word and hour-of-work count.
- Reworked front page.
- Reworked syntax highlighting scheme in web version.
- Header on web version now collapses after scrolling to enhance reading experience. The header appears once users scroll up slightly.
- New chapter idea (Chapter 11 - Convolutional Neural Networks)
- Changed syntax highlighting architecture to [this](https://github.com/JC-ProgJava/SyntaxHighlighter) (made by me! 🙂).
- Fixed #14, #17, #19, #23, #24.
- Modified font size in mobile viewports.
- Added support for printing chapters.

1 January 2021

- 🎉 Happy New Year! 🎉

<pre>
==========     ===========    ==========     //

          ||  ||         ||             ||   //

          ||  ||         ||             ||   //

          ||  ||         ||             ||   //

 ==========   ||         ||   ==========     //

||            ||         ||  ||              //

||            ||         ||  ||              //

||            ||         ||  ||              //

 ==========   ===========     ==========     //
</pre>

- Updated `preface.html` to make `<aside>` section match `<p>` text size on laptop screen viewports.
- LN 205 in `activation.html`: fixed equation for derivative in Chapter Summary (in limit '∆x -> 0' instead of 'x -> 0')
- Will be taking a break, be back soon! ☕️

29 December 2020

- Finished Chapter 6 - Validation Datasets 🙂
- Pushed Chapter 6 to the web. 🚀

20-26 December 2020

- Merry Christmas! 🎄
- Patching web version to improve mobile compatibility.
- Gathering resources for Chapter 6 - Validation Datasets

19 December 2020

- Finished Chapter 5 - Overfitting and Underfitting.
- Pushed Chapter 5 to the web. 🚀
- Added "Overfitting_Underfitting" code folder.

7 December 2020

- Finished Task 1 - Recognizing Digits
- Pushed all chapters (Chpt. 1-4 & Task 1) to the web. 🚀
- Added "Project1"(Task 1) code folder.

18 November 2020

- Challenge section in Task 1 - Recognizing Digits almost complete: only Solution and Visualizing Weights parts incomplete.

13 November 2020

- Full re-edit of book.
- Planning in Objectives file locally.

12 November 2020

- Finished Task 1 - Recognizing Digits
- Scheduled Challenge in Task 1 - Recognizing Digits
- Scheduled Book Summary

8 November 2020

- Added "Notation" and "Derivative" sections to Activation Functions
- Added Chapter Summaries.

6 November 2020

- Added Contents
- Added Citations
- Added License Notice
- Added Contact
- Reorder: swapped order of Neural Networks and Learning, and Activation Functions
- Finished Activation Functions

31 October 2020

- Finished Chapter 3 - Neural Networks and Learning
- Scheduled Task 1 - Recognizing Digits

29 October 2020

- Updated Preface
- Updated Perceptron
- Switch to Amiri font.
- Updated name of Chapter 3 to "Neural Networks and Learning"
- Started Chapter 3 - Neural Networks and Learning
- Scheduled Chapter 4 - Activation Functions

27 October 2020

- Finished Chapter 1 - Introduction.
- Finished Chapter 2 - Perceptron.
- Scheduled Further Reading.
- Scheduled References.
- Updated foreword.

25 October 2020

- Finished foreword.
- Scheduled Chapter 1 - Perceptron.
