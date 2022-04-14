// Token types (see the Java version for more info.)
const TokenType = Object.freeze({
    IDENTIFIER: "IDENTIFIER",
    KEYWORD: "KEYWORD",
    NUMBER: "NUMBER",
    STRING: "STRING",
    SPACE: "SPACE",
    NEWLINE: "NEWLINE",
    TAB: "TAB",
    IMPORTNAME: "IMPORTNAME",
    HEADDATATYPE: "HEADDATATYPE",
    CONSTANT: "CONSTANT",
    OTHERPUNCTUATION: "OTHERPUNCTUATION",
    JAVADOC: "JAVADOC",
    ANNOTATION: "ANNOTATION",
    METHODNAME: "METHODNAME",
    COMMENT: "COMMENT",
    CLASSNAME: "CLASSNAME",
    DATATYPE: "DATATYPE",
    LITERAL: "LITERAL",
});

class Parser {
    tokens = [];
    outCodeHTML = "";
    stylesheet = "";
    outPlain = "";
    styles = {};
    style;

    constructor(tokens, style) {
        this.tokens = tokens;
        this.styles["Normal"] = [
            "rgb(244, 242, 240)",
            "#003399",
            "black",
            "#1740E6",
            "#106B10",
            "black",
            "black",
            "black",
            "#006699",
            "#034524",
            "black",
            "#660E7A",
            "#bf8f1d",
            "#808000",
            "black",
            "#106B10",
            "black",
            "#003399",
            "black",
        ];
        this.styles["Exotic"] = [
            "inherit",
            "#136F75",
            "#C3301E",
            "#1F5D15",
            "#8A2428",
            "black",
            "black",
            "black",
            "#2E690A",
            "#BF005C",
            "#940066",
            "#2E690A",
            "#8A2428",
            "#8A2428",
            "#8A07B3",
            "#8A2428",
            "#C3301E",
            "#136F75",
            "#C3301E",
        ];
        this.styles["AtomOneDark"] = [
            "#282C34",
            "#C678DD; font-weight: normal",
            "#abb2bf",
            "#d19a66",
            "#98c379",
            "#abb2bf",
            "#abb2bf",
            "#abb2bf",
            "#C678DD",
            "#abb2bf",
            "#abb2bf",
            "#d19a66",
            "#5c6370; font-style: italic",
            "#c678dd",
            "#61aeee",
            "#5c6370; font-style: italic",
            "#d19a66",
            "#56b6c2; font-weight: normal",
            "#e6c07b",
        ];
        this.style = style;
        this.stylesheet = `code {
                           display: block;
                           background-color: %s;
                           transition: background-color 200ms;
                         }

                         code span {
                           display: inline;
                           white-space: pre;
                         }

                         .keyword {
                           color: %s;
                           transition: color 200ms;
                         }

                         .identifier {
                           color: %s;
                           transition: color 200ms;
                         }

                         .number {
                           color: %s;
                           transition: color 200ms;
                         }

                         .string {
                           color: %s;
                           transition: color 200ms;
                         }

                         .space {
                           color: %s;
                           transition: color 200ms;
                         }

                         .newline {
                           color: %s;
                           transition: color 200ms;
                         }

                         .tab {
                           color: %s;
                           transition: color 200ms;
                         }

                         .importname {
                           color: %s;
                           transition: color 200ms;
                         }

                         .headdatatype {
                           color: %s;
                           transition: color 200ms;
                         }

                         .otherpunctuation {
                           color: %s;
                           transition: color 200ms;
                         }

                         .constant {
                           color: %s;
                           transition: color 200ms;
                         }

                         .javadoc {
                           color: %s;
                           transition: color 200ms;
                         }

                         .annotation {
                           color: %s;
                           transition: color 200ms;
                         }

                         .methodname {
                           color: %s;
                           transition: color 200ms;
                         }

                         .comment {
                           color: %s;
                           transition: color 200ms;
                         }

                         .datatype {
                           color: %s;
                           transition: color 200ms;
                         }

                         .literal {
                           color: %s;
                           transition: color 200ms;
                         }

                         .classname {
                           color: %s;
                           transition: color 200ms;
                         }
                       `;
    }

    parse() {
        for (let index = 0; index < this.styles[this.style].length; index++) {
            this.stylesheet = this.stylesheet.replace(
                "%s",
                this.styles[this.style][index]
            );
        }

        for (let index = 0; index < this.tokens.length; index++) {
            const token = this.tokens[index];
            switch (token.type) {
                case "IDENTIFIER":
                case "KEYWORD":
                case "NUMBER":
                case "STRING":
                case "IMPORTNAME":
                case "HEADDATATYPE":
                case "OTHERPUNCTUATION":
                case "JAVADOC":
                case "ANNOTATION":
                case "CONSTANT":
                case "METHODNAME":
                case "COMMENT":
                case "DATATYPE":
                case "LITERAL":
                case "CLASSNAME":
                    this.outCodeHTML +=
                        '<span class="' +
                        String(token.type).toLowerCase() +
                        '">' +
                        this.cleanse(token.text) +
                        "</span>";
                    this.outPlain += token.text;
                    break;
                case "NEWLINE":
                    this.outCodeHTML +=
                        '<span class="' +
                        String(token.type).toLowerCase() +
                        '"><br /></span>';
                    this.outPlain += "\n";
                    break;
                case "TAB":
                    this.outCodeHTML +=
                        '<span class="' +
                        String(token.type).toLowerCase() +
                        '">    </span>';
                    this.outPlain += "\t";
                    break;
                case "SPACE":
                    this.outCodeHTML +=
                        '<span class="' + String(token.type).toLowerCase() + '"> </span>';
                    this.outPlain += " ";
                    break;
                default:
                    console.log("Unexpected enum: " + String(token.type));
            }
        }
        return [this.outCodeHTML, this.stylesheet, this.outPlain];
    }

    cleanse(text) {
        return text
            .replaceAll("&", "&amp;")
            .replaceAll(" ", "&nbsp;")
            .replaceAll("{", "&lbrace;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;");
    }
}

class Token {
    constructor(text, line, col, type) {
        this.text = text;
        this.line = line;
        this.col = col;
        this.type = type;
    }
}

class Tokenizer {
    tokens = [];
    keywords = [];
    literals = [];
    dataTypes = [];
    classKeywords = [];
    line = 1;
    col = 1;
    start = 0;
    current = 0;
    sourceCode = "";

    constructor() {
        this.keywords = [
            "abstract",
            "assert",
            "boolean",
            "break",
            "byte",
            "case",
            "catch",
            "char",
            "class",
            "continue",
            "const",
            "default",
            "do",
            "double",
            "else",
            "enum",
            "exports",
            "extends",
            "final",
            "finally",
            "float",
            "for",
            "goto",
            "if",
            "implements",
            "import",
            "instanceof",
            "int",
            "interface",
            "long",
            "module",
            "native",
            "new",
            "non-sealed",
            "package",
            "private",
            "protected",
            "public",
            "requires",
            "return",
            "short",
            "static",
            "strictfp",
            "super",
            "switch",
            "synchronized",
            "this",
            "throw",
            "throws",
            "transient",
            "try",
            "var",
            "void",
            "volatile",
            "while",
            "yield",
            "sealed",
            "record",
            "permits",
            "System",
        ];

        this.literals = ["true", "false", "null"];

        this.dataTypes = [
            "Byte",
            "Short",
            "Integer",
            "Long",
            "Float",
            "Double",
            "Boolean",
            "Character",
        ];

        this.classKeywords = ["class", "interface", "enum", "extends", "implements"];
    }

    peek() {
        if (this.current + 1 < this.sourceCode.length) {
            return this.sourceCode.charAt(this.current + 1);
        } else {
            return "\0";
        }
    }

    isAlphabet(currentChar) {
        return String(currentChar).match(new RegExp("[a-zA-Z_$]")) != null;
    }

    isIdentifierEnding(currentChar) {
        return String(currentChar).match(new RegExp("[a-zA-Z_$0-9]")) != null;
    }

    notAtEnd() {
        return this.current < this.sourceCode.length;
    }

    tokenize(code) {
        this.sourceCode = code;
        if (this.sourceCode.charAt(this.sourceCode.length - 1) !== "\n") {
            this.sourceCode += "\n";
        }

        while (this.notAtEnd()) {
            let currentChar = this.sourceCode.charAt(this.current);
            if (this.isNumerical(currentChar)) {
                while (this.notAtEnd()) {
                    if (this.isNumerical(this.peek()) || this.peek() === ".") {
                        this.col++;
                        this.current++;
                    } else {
                        break;
                    }
                }

                this.tokens.push(
                    new Token(
                        this.sourceCode.substring(this.start, this.current + 1),
                        this.line,
                        this.col,
                        TokenType.NUMBER
                    )
                );
            } else if (this.isIdentifierEnding(currentChar)) {
                while (this.notAtEnd() && this.isIdentifierEnding(this.peek())) {
                    this.col++;
                    this.current++;
                }
                if (
                    this.keywords.includes(
                        this.sourceCode.substring(this.start, this.current + 1).trim()
                    )
                ) {
                    this.tokens.push(
                        new Token(
                            this.sourceCode.substring(this.start, this.current + 1),
                            this.line,
                            this.col,
                            TokenType.KEYWORD
                        )
                    );
                } else if (
                    this.literals.includes(
                        this.sourceCode.substring(this.start, this.current + 1).trim()
                    )
                ) {
                    this.tokens.push(
                        new Token(
                            this.sourceCode.substring(this.start, this.current + 1),
                            this.line,
                            this.col,
                            TokenType.LITERAL
                        )
                    );
                } else if (
                    this.dataTypes.includes(
                        this.sourceCode.substring(this.start, this.current + 1).trim()
                    )
                ) {
                    this.tokens.push(
                        new Token(
                            this.sourceCode.substring(this.start, this.current + 1),
                            this.line,
                            this.col,
                            TokenType.DATATYPE
                        )
                    );
                } else {
                    this.tokens.push(
                        new Token(
                            this.sourceCode.substring(this.start, this.current + 1),
                            this.line,
                            this.col,
                            TokenType.IDENTIFIER
                        )
                    );
                }
            } else {
                switch (currentChar) {
                    case " ":
                    case "\t":
                        this.col++;
                        this.current++;
                        continue;
                    case "\n":
                        this.tokens.push(
                            new Token(
                                this.sourceCode.substring(this.start, this.current + 1),
                                this.line,
                                this.col,
                                TokenType.NEWLINE
                            )
                        );
                        this.line++;
                        this.col = 0;
                        break;
                    case "@":
                        this.annotation();
                        this.current++;
                        this.tokens.push(
                            new Token(
                                this.sourceCode.substring(this.start, this.current + 1),
                                this.line,
                                this.col,
                                TokenType.ANNOTATION
                            )
                        );
                        break;
                    case '"':
                        // Supports multiline strings
                        if (this.peek() === '"' && this.peekNext() === '"') {
                            this.current += 2;
                            this.multilineString();
                        } else {
                            this.string();
                        }
                        this.current++;
                        this.tokens.push(
                            new Token(
                                this.sourceCode.substring(this.start, this.current + 1),
                                this.line,
                                this.col,
                                TokenType.STRING
                            )
                        );
                        break;
                    case "'":
                        this.character();
                        this.current++;
                        this.tokens.push(
                            new Token(
                                this.sourceCode.substring(this.start, this.current + 1),
                                this.line,
                                this.col,
                                TokenType.STRING
                            )
                        );
                        break;
                    case "/":
                        if (this.peek() === "/") {
                            this.comment();
                            this.current++;
                            this.tokens.push(
                                new Token(
                                    this.sourceCode.substring(this.start, this.current + 1),
                                    this.line,
                                    this.col,
                                    TokenType.COMMENT
                                )
                            );
                        } else if (this.peek() === "*") {
                            if (this.peekNext() !== "*" || this.peekAfterNext() === "/") {
                                this.multilineComment();
                                this.current++;
                                this.tokens.push(
                                    new Token(
                                        this.sourceCode.substring(this.start, this.current + 1),
                                        this.line,
                                        this.col,
                                        TokenType.COMMENT
                                    )
                                );
                            } else {
                                this.multilineComment();
                                this.current++;
                                this.tokens.push(
                                    new Token(
                                        this.sourceCode.substring(this.start, this.current + 1),
                                        this.line,
                                        this.col,
                                        TokenType.JAVADOC
                                    )
                                );
                            }
                        } else {
                            this.tokens.push(
                                new Token(
                                    this.sourceCode.substring(this.start, this.current + 1),
                                    this.line,
                                    this.col,
                                    TokenType.OTHERPUNCTUATION
                                )
                            );
                        }
                        break;
                    case ";":
                    case "(":
                    case ")":
                    case "{":
                    case "}":
                    case "<":
                    case ">":
                    case "[":
                    case "]":
                    case ",":
                    case "*":
                    case "=":
                    case "+":
                    case "-":
                    case "%":
                    case "!":
                    case "~":
                    case "&":
                    case "|":
                    case "?":
                    case ":":
                    case "^":
                    case ".":
                        this.tokens.push(
                            new Token(
                                this.sourceCode.substring(this.start, this.current + 1),
                                this.line,
                                this.col,
                                TokenType.OTHERPUNCTUATION
                            )
                        );
                        break;
                    default:
                        console.log(
                            "Could not interpret character: '" +
                            currentChar +
                            "'. [ln: " +
                            this.line +
                            "]"
                        );
                        break;
                }
            }
            this.col++;
            this.current++;
            this.start = this.current;
        }

        for (let index = 0; index < this.tokens.length; index++) {
            this.current = this.tokens[index].col;
            if (
                this.tokens[index].text === "import" ||
                this.tokens[index].text === "package"
            ) {
                let indexstart = index + 1;
                while (this.tokens[indexstart].text !== ";") {
                    let token = this.tokens[indexstart];
                    this.tokens.splice(
                        indexstart,
                        0,
                        new Token(token.text, token.line, token.col, TokenType.IMPORTNAME)
                    );
                    this.tokens.splice(indexstart + 1, 1);
                    indexstart++;
                }
            } else if (
                this.tokens[index].type === TokenType.IDENTIFIER ||
                this.tokens[index].text.match(/[})\]]/g) != null
            ) {
                if (
                    this.tokens.length > index + 1 &&
                    this.tokens[index + 1].text === "."
                ) {
                    let indexstart = index + 1;
                    while (
                        this.tokens[indexstart].type !== TokenType.OTHERPUNCTUATION ||
                        this.tokens[indexstart].text === "."
                        ) {
                        if (this.tokens[indexstart].text === ".") {
                            indexstart++;
                            continue;
                        }

                        if (indexstart + 1 === this.tokens.length) {
                            break;
                        }
                        let token = this.tokens[indexstart];
                        this.tokens.splice(
                            indexstart,
                            0,
                            new Token(
                                token.text,
                                token.line,
                                token.col,
                                TokenType.HEADDATATYPE
                            )
                        );
                        this.tokens.splice(indexstart + 1, 1);
                        indexstart++;
                    }
                }
            }

            if (
                this.notAtEnd() &&
                (this.tokens[index].type === TokenType.IDENTIFIER ||
                    this.tokens[index].type === TokenType.HEADDATATYPE) &&
                this.tokens[index + 1].text === "("
            ) {
                let token = this.tokens[index];
                this.tokens.splice(
                    index,
                    0,
                    new Token(token.text, token.line, token.col, TokenType.METHODNAME)
                );
                this.tokens.splice(index + 1, 1);
            }

            if (
                index - 1 > 0 &&
                this.classKeywords.includes(this.tokens[index - 1].text)
            ) {
                while (
                    this.tokens[index].type === TokenType.SPACE ||
                    this.tokens[index].type === TokenType.NEWLINE ||
                    this.tokens[index].type === TokenType.TAB
                    ) {
                    index++;
                }
                let token = this.tokens[index];
                this.tokens.splice(
                    index,
                    0,
                    new Token(token.text, token.line, token.col, TokenType.CLASSNAME)
                );
                this.tokens.splice(index + 1, 1);
            }

            if (
                this.isUppercase(this.tokens[index].text) &&
                (this.tokens[index].type === TokenType.HEADDATATYPE ||
                    this.tokens[index].type === TokenType.IDENTIFIER)
            ) {
                let token = this.tokens[index];
                this.tokens.splice(
                    index,
                    0,
                    new Token(token.text, token.line, token.col, TokenType.CONSTANT)
                );
                this.tokens.splice(index + 1, 1);
            }
        }

        return this.tokens;
    }

    peekAfterNext() {
        if (this.current + 3 < this.sourceCode.length) {
            return this.sourceCode.charAt(this.current + 3);
        } else {
            return "\0";
        }
    }

    annotation() {
        while (this.notAtEnd()) {
            if (this.peek() === " ") {
                break;
            } else if (this.peek() === "\n") {
                this.line++;
                this.col = 1;
                break;
            }
            this.col++;
            this.current++;
        }
    }

    multilineString() {
        while (this.notAtEnd()) {
            if (
                this.sourceCode.charAt(this.current) === '"' &&
                this.peek() === '"' &&
                this.peekNext() === '"'
            ) {
                if (
                    (this.current - 1 >= 0 &&
                        this.sourceCode.charAt(this.current - 1) !== "\\") ||
                    this.current === 0
                ) {
                    break;
                }
            } else if (this.peek() === "\n") {
                this.line++;
                this.col = 1;
            }
            this.col++;
            this.current++;
        }
        this.col++;
        this.current++;
    }

    multilineComment() {
        while (this.notAtEnd()) {
            if (this.peek() === "*" && this.peekNext() === "/") {
                break;
            } else if (this.peek() === "\n") {
                this.line++;
                this.col = 1;
            }
            this.col++;
            this.current++;
        }
        this.col++;
        this.current++;
    }

    peekNext() {
        if (this.current + 2 < this.sourceCode.length) {
            return this.sourceCode.charAt(this.current + 2);
        } else {
            return "\0";
        }
    }

    isUppercase(text) {
        return text.match(new RegExp("^[A-Z_$][A-Z_$0-9]*$")) != null;
    }

    comment() {
        while (this.notAtEnd() && this.peek() !== "\n") {
            this.col++;
            this.current++;
        }
    }

    character() {
        while (
            this.notAtEnd() &&
            (this.peek() !== "'" || this.sourceCode.charAt(this.current) === "\\")
            ) {
            if (this.sourceCode.charAt(this.current) === "\\") {
                this.col++;
                this.current++;
                if (this.peek() === "'") {
                    break;
                }
                continue;
            }
            this.col++;
            this.current++;
        }
    }

    string() {
        while (
            this.notAtEnd() &&
            (this.peek() !== '"' || this.sourceCode.charAt(this.current) === "\\")
            ) {
            if (this.sourceCode.charAt(this.current) === "\\") {
                this.col++;
                this.current++;
                if (this.peek() === '"') {
                    break;
                }
                continue;
            }
            this.col++;
            this.current++;
        }
    }

    isNumerical(currentChar) {
        return String(currentChar).match(new RegExp("[0-9 ]")) != null;
    }
}

const head = document.head || document.getElementsByTagName("head")[0];
const style = document.createElement("style");

head.appendChild(style);

window.addEventListener('load', function () {
    highlight("Normal");
});

function highlight(theme) {
    let code = document.getElementsByTagName("code");
    let css;
    for (let i = 0; i < code.length; i++) {
        let tokensList = new Tokenizer().tokenize(code[i].innerText);
        let parser = new Parser(tokensList, theme);
        let output = parser.parse();
        css = output[1];
        code[i].innerHTML = output[0];
    }

    style.appendChild(document.createTextNode(css));
}

function setTheme(theme) {
    let styles = new Parser(null, null).styles[theme];
    let stylesheet = new Parser(null, null).stylesheet;
    for (let index = 0; index < styles.length; index++) {
        stylesheet = stylesheet.replace(
            "%s",
            styles[index]
        );
    }

    style.appendChild(document.createTextNode(stylesheet));
}
