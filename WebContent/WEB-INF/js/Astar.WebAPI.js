function ConvertToHexString(SrcStr) {
    var szSrcValue = SrcStr;
    var szDestValue = "";
    var nCode = 0;
    var szChar = "";

    if (szSrcValue != null) {
        for (var i = 0; i < szSrcValue.length; i++) {
            nCode = szSrcValue.charCodeAt(i);
            szChar = WordToHex(UTF16Encode(nCode));
            szDestValue += szChar;
        }
    }
    return szDestValue;
}

function UTF16Encode(pUnicode) {
    var t_text = new Array(2);
    if (pUnicode >= 0x10000) {
        t_text[0] = (0xD800 | ((pUnicode - 0x10000) >> 10));
        t_text[1] = (0xDC00 | ((pUnicode - 0x10000) & 0x3FF));
        return String.fromCharCode(t_text[0], t_text[1]);
    }
    else {
        t_text[0] = pUnicode;
        t_text[1] = 0x0;
        return String.fromCharCode(t_text[0]);
    }
}

function WordToHex(Word) {
    var nCode = Word.charCodeAt(0);
    var szHex = nCode.toString(16).toUpperCase();
    while (4 - szHex.length > 0) {
        szHex = "0" + szHex;
    }
    return szHex;
}