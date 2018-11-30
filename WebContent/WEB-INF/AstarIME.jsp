<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>輸入視窗</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta http-equiv="Pragma" content="no-cache">
<meta name="viewport" content="width=device-width"/>
<style>.TD_PIC { width: 32px; height: 32px; text-align: center; vertical-align: middle; }</style>
<script type="text/javascript" src="<c:url value='/js/jquery-1.11.0.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/Astar.LibMisc.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/Astar.WebEditorLib.js'/>"></script>
<script language="javascript" type="text/javascript">
// var DEF_DATA_WEB_PREFIX = "http://tnsweb.bot.com.tw";
var DEF_DATA_WEB_PREFIX = "/bottb/Astar"; 
var DEF_ALERT_WORDS_AMOUNT = false;
var DEF_UNICODE_BMP_EUDC_ONLY = false; // only display Unicode BMP EUDC
var DEF_UNICODE_BMP_EUDC_BEGIN = 0xE000;
var DEF_UNICODE_BMP_EUDC_END = 0xF848;
var DEF_FULL_CNS = true;
var DEF_WORDS_PERPAGE = 20;
// Query type
var DEF_QUERY_ZUIN = 1;
var DEF_QUERY_CHANGJEI = 2;
var DEF_QUERY_UNICODE = 3;
var DEF_QUERY_CNS = 4;
var DEF_QUERY_BROWSE = 5;
// IME type
var DEF_IME_ZUIN = 1;
var DEF_IME_CHANGJEI = 2;
// IME mapping type
var DEF_IMEMAPTYPE_DISPLAY = 1;
var DEF_IMEMAPTYPE_QUERY = 2;
// IME mapping data
var DEF_IMEMAP_ZUIN = [
	["1", "ㄅ"], ["Q", "ㄆ"], ["A", "ㄇ"], ["Z", "ㄈ"], ["2", "ㄉ"], ["W", "ㄊ"], ["S", "ㄋ"], ["X", "ㄌ"], ["3", "ˇ"],
	["E", "ㄍ"], ["D", "ㄎ"], ["C", "ㄏ"], ["4", "ˋ"], ["R", "ㄐ"], ["F", "ㄑ"], ["V", "ㄒ"], ["5", "ㄓ"], ["T", "ㄔ"],
	["G", "ㄕ"], ["B", "ㄖ"], ["6", "ˊ"], ["Y", "ㄗ"], ["H", "ㄘ"], ["N", "ㄙ"], ["7", "˙"], ["U", "ㄧ"], ["J", "ㄨ"],
	["M", "ㄩ"], ["8", "ㄚ"], ["I", "ㄛ"], ["K", "ㄜ"], [",", "ㄝ"], ["9", "ㄞ"], ["O", "ㄟ"], ["L", "ㄠ"], [".", "ㄡ"],
	["0", "ㄢ"], ["P", "ㄣ"], [";", "ㄤ"], ["/", "ㄥ"], ["-", "ㄦ"]
];
var DEF_IMEMAP_CHANGJEI = [
	["Q", "手"], ["A", "日"], ["Z", "重"], ["W", "田"], ["S", "尸"], ["X", "難"], ["E", "水"], ["D", "木"], ["C", "金"],
	["R", "口"], ["F", "火"], ["V", "女"], ["T", "廿"], ["G", "土"], ["B", "月"], ["Y", "卜"], ["H", "竹"], ["N", "弓"],
	["U", "山"], ["J", "十"], ["M", "一"], ["I", "戈"], ["K", "大"], ["O", "人"], ["L", "中"], ["P", "心"]
];
// XML Tags and attributes
var DEF_TAG_ROOT = "ASTAR";
var DEF_TAG_WORD = "WORD";
var DEF_ATT_UNICODE = "unicode";
// Word code type
var DEF_WORDTYPE_OTHER = 0;
var DEF_WORDTYPE_BMP_EUDC = 1;
var DEF_WORDTYPE_PLANE2 = 2;
var DEF_WORDTYPE_PLANE15 = 3;
var DEF_WORDTYPE_PLANE16 = 4;
// Global variables
var gPageIndex = 0;
var gActiveXCtrl = null;
var gWebPicDir = DEF_DATA_WEB_PREFIX + "/WebImg/";
var gWordCodes = null;
var gIsBrowse = false;
var gTargetEditor = null;
var gWebEditor = null;
var gIsWordSelected = false;

function IsNullOrUndefined(pObj) {
    return (pObj == null || pObj == undefined);
}

function IsNullOrEmptyString(pValue) {
    return (pValue == null || pValue.length == 0);
}

function InitPage() {
    var szTargetEditorId = GetLocationParameter("EditorId", "");

    if (IsNullOrUndefined(opener) || IsNullOrEmptyString(szTargetEditorId)) {
        CloseWindowAlert("此網頁無法直接瀏覽或開啟，視窗即將關閉！");
        return;
    }

    opener.$("#" + szTargetEditorId).each(function () {
        gTargetEditor = $(this)[0];
        return; // find 1st element
    });

    if (IsNullOrUndefined(gTargetEditor)) {
        CloseWindowAlert("無法取得目標輸入元件，視窗即將關閉！");
        return;
    }

    var tOpenerEditors = opener.GetAllAstarWebEditors();
    if (tOpenerEditors == null || tOpenerEditors.length == 0) {
        CloseWindowAlert("無法取得所有 AstarWebEditor，視窗即將關閉！");
        return;
    }

    for (var i = 0; i < tOpenerEditors.length; i++) {
        if (tOpenerEditors[i].id == gTargetEditor.id) {
            gWebEditor = tOpenerEditors[i];
            break;
        }
    }
    if (gWebEditor == null) {
        CloseWindowAlert("無法取得目標 AstarWebEditor，視窗即將關閉！");
        return;   
    }

    SetPageAmount(0);
    GenGotoComboBox(0);
    SetQueryItemsStatus();
}

function SetQueryItemsStatus() {
    /*
    if (DEF_UNICODE_BMP_EUDC_ONLY) {
        document.getElementById('CNSPlane').disabled = true;
        document.getElementById('CNSCode').disabled = true;
        document.getElementById('Query4').disabled = true;
    }
    */
}

function GetLocationParameter(pName, pDefault) {
    if (IsNullOrEmptyString(pName)) {
        return "";
    }

    if (IsNullOrEmptyString(window.location.search) ||
		window.location.search.length <= 2) { // 2 for split
        return pDefault;
    }

    var szSearch = window.location.search;
    szSearch = szSearch.substring(1, szSearch.length);
    var szGroups = szSearch.split("&");
    if (szGroups != null) {
        for (var i = 0; i < szGroups.length; i++) {
            var szKeyValues = szGroups[i].split("=");
            if (szKeyValues.length == 2 && szKeyValues[0].toUpperCase() == pName.toUpperCase()) {
                return szKeyValues[1];
            }
        }
    }
    return pDefault;
}

function CloseWindowAlert(pMsg) {
    alert(pMsg);
    window.close();
}

function SetPageAmount(pAmount) {
    document.getElementById('PageAmount').innerText = pAmount;
}

function GenGotoComboBox(pPageAmount) {
    var tNode = document.getElementById('Goto');
    while (tNode.options.length > 0) {
        tNode.options.remove(0);
    }
    if (pPageAmount <= 0) {
        tNode.disabled = true;
        return;
    }

    tNode.disabled = false;
    for (var i = 0; i < pPageAmount; i++) {
        tNode.options[i] = new Option(i + 1, i + 1);
    }
}

function SetGotoComboBoxIndex(pIndex) {
    document.getElementById('Goto').selectedIndex = pIndex;
}

// if pStartIdx<0, cleaing words
function DisplayWords(pStartIdx) {
    var tTable = document.getElementById('WordsTable');
    var tRow = null;
    var nCurrIdx = 0;
    var nCurrCode = 0;
    var szStr = "";
    var tElement = null;

    /*if (gIsBrowse && !DEF_UNICODE_BMP_EUDC_ONLY) {
        var szXml = QueryCandidateXml(DEF_QUERY_BROWSE, pStartIdx);
        gWordCodes = ParseCandidateXml(szXml);
    }*/

    for (var i = 1; i < tTable.rows.length; i++) {
        tRow = tTable.rows[i];
        for (var j = 0; j < tRow.cells.length; j++) {
            if (gIsBrowse && !DEF_UNICODE_BMP_EUDC_ONLY) {
                nCurrIdx = ((i - 1) * tRow.cells.length) + j;
            } else {
                nCurrIdx = ((i - 1) * tRow.cells.length) + j + pStartIdx;
            }
            
            if (gWordCodes == null || pStartIdx < 0 || nCurrIdx >= gWordCodes.length) {
                tRow.cells[j].bgColor = "";
                tRow.cells[j].innerHTML = "&nbsp;";
                continue;
            }
            /* 顯示候選字 */
            nCurrCode = gWordCodes[nCurrIdx];
            szStr = MergePicFilename(nCurrCode);
            tElement = CreateImgElement(szStr, nCurrCode);
            tRow.cells[j].innerHTML = "";
            tRow.cells[j].bgColor = "#C0C0C0";
            tRow.cells[j].id = "row-" + i + "-" + j + "-" + nCurrCode;
            $("#" + tRow.cells[j].id).append(tElement);
            tRow.cells[j].onclick = CellClick;
        }
    }
}

function CellClick() {
    var szParts = new String(this.id).split("-");
    var nCode = parseInt(szParts[szParts.length - 1]);
    WordSelected(nCode);
}

function MergePicFilename(pWordCode) {
    var nCodeType = GetWordCodeType(pWordCode);
    var szHexStr = pWordCode.toString(16).toUpperCase();

    switch (nCodeType) {
        case DEF_WORDTYPE_PLANE2:
            return "02/300000" + szHexStr + "2A1800000040.GIF";
        case DEF_WORDTYPE_PLANE15:
            return "0F/300000" + szHexStr + "2A1800000040.GIF";
        case DEF_WORDTYPE_PLANE16:
            return "10/30000" + szHexStr + "2A1800000040.GIF";
        case DEF_WORDTYPE_BMP_EUDC:
        default:
            {
                while (szHexStr.length < 4) {
                    szHexStr = "0" + szHexStr;
                }
                return "00/100" + szHexStr + "2A1800000040.GIF";
            }
    }
}

/*候選字*/
function CreateImgElement(pPicFilename, pWordCode) {
    var tElement = document.createElement("img");
    tElement.setAttribute("src", gWebPicDir + pPicFilename);
    tElement.setAttribute("onclick", "WordSelected(" + pWordCode + ")");
    tElement.setAttribute("border", "0");
    tElement.setAttribute("style", "cursor:hand");
    return tElement;
}

function WordSelected(pWordCode) {
    if (!gIsWordSelected) {
        gIsWordSelected = true;
        var nNewCode = gWebEditor.SetCodeMapping(pWordCode);
        gTargetEditor.value += String.fromCharCode(nNewCode); // 這裡要改成用 insert by cursor position
        opener.ReloadWebFont(gTargetEditor, gWebEditor.GetAllCodeMappings());
        window.close();
    }
}

function GotoPage(pPageIdx) {
    SetGotoComboBoxIndex(pPageIdx);
    gPageIndex = pPageIdx;
    DisplayWords(gPageIndex * DEF_WORDS_PERPAGE);
}

function PrePage() {
    if (gPageIndex - 1 < 0) {
        alert('已經是第一頁');
        return;
    }
    GotoPage(gPageIndex - 1);
}

function NextPage() {
    var nPageAmount = parseInt(document.getElementById('PageAmount').innerText);
    if (gPageIndex + 1 >= nPageAmount) {
        alert('已經是最後一頁');
        return;
    }
    GotoPage(gPageIndex + 1);
}

function Query(pQueryType) {
    var szKey = "";

    gIsBrowse = false;
    if (pQueryType == DEF_QUERY_UNICODE) {
        szKey = GetObjectValue("Unicode", "請輸入Unicode字碼");
        if (szKey.length == 0) {
            return;
        }
    }
    else if (pQueryType == DEF_QUERY_ZUIN) {
        szKey = GetObjectValue("ZuIn", "請輸入注音字根");
        if (szKey.length == 0) {
            return;
        }
        szKey = ImeMapping(szKey, DEF_IMEMAP_ZUIN, DEF_IMEMAPTYPE_QUERY);
    }
    else if (pQueryType == DEF_QUERY_CHANGJEI) {
        szKey = GetObjectValue("ChangJei", "請輸入倉頡字根");
        if (szKey.length == 0) {
            return;
        }
        szKey = ImeMapping(szKey, DEF_IMEMAP_CHANGJEI, DEF_IMEMAPTYPE_QUERY);
    }
    else {
        alert("不支援的查詢方式");
        return;
    }

    QueryCandidateXml(pQueryType, szKey);
}

function ProcessQueryResult(szXml){
    gWordCodes = null;
    gPageIndex = 0;

    if (szXml != null || szXml.length > 0) {
        gWordCodes = ParseCandidateXml(szXml);
    }

    if (gWordCodes == null || gWordCodes.length == 0) {
        DisplayWords(-1);
        SetPageAmount(0);
        GenGotoComboBox(0);
        alert("查詢不到資料");
        return;
    }

    var nWordCodesAmount = gWordCodes.length;
    var nPageAmount = Math.floor(nWordCodesAmount / DEF_WORDS_PERPAGE);
    if (nWordCodesAmount % DEF_WORDS_PERPAGE != 0) {
        nPageAmount++;
    }
    DisplayWords(0);
    SetPageAmount(nPageAmount);
    GenGotoComboBox(nPageAmount);
    if (DEF_ALERT_WORDS_AMOUNT) {
        alert("共查詢【" + gWordCodes.length + "】個字");
    } else {
        SetWordAmount(gWordCodes.length);
    }
}

function SetWordAmount(WordAmount) {
    document.getElementById("TotalWords").innerHTML = "共" + WordAmount + "個字。";
}

function GetObjectValue(pObjId, pErrMsg) {
    var tObj = document.getElementById(pObjId);
    if (tObj.value.length == 0) {
        alert(pErrMsg);
        tObj.focus();
        return "";
    }
    return tObj.value;
}

function QueryCandidateXml(pQueryType, pKey) {
    var szURL = DEF_DATA_WEB_PREFIX + "/GetCandidateWordXml.aspx" +
    "?Type=" + encodeURIComponent(pQueryType) +
    "&Key=" + encodeURIComponent(pKey);
    //doAjax(szURL, ProcessQueryResult);
    var szXml = GetServerXmlSync(szURL);
    ProcessQueryResult(szXml);
}

function MergeCandidateXml(pHexUnicode) {
    return "<" + DEF_TAG_ROOT + ">" + MergeWordNodeXml(pHexUnicode) + "</" + DEF_TAG_ROOT + ">";
}

function MergeWordNodeXml(pHexUnicode) {
    // <WORD unicode="XXXX" />
    return "<" + DEF_TAG_WORD + " " + DEF_ATT_UNICODE + "=\"" + pHexUnicode + "\" />";
}

function GetWordUnicode(pXNodes, pIdx) {
    var nCode = 0;
    if (pXNodes != null && pXNodes.item(pIdx) != null) {
        var szHex = pXNodes.item(pIdx).getAttribute(DEF_ATT_UNICODE);
        if (szHex != null && szHex.length > 0) {
            nCode = parseInt(szHex, 16);
        }
    }
    return nCode;
}

function CreateMSXmlDom() {
    return new ActiveXObject("Microsoft.XMLDOM");
}

function ParseCandidateXml(pXml) {
    var tXDoc = $.parseXML(pXml);
    $xml = $(tXDoc);
    var nLength = $xml.find("WORD").length;
    var tUnicodes = null;
    if (nLength > 0) {
        tUnicodes = new Array();
        for (var i = 0; i < nLength; i++) {
            var szHex = $xml.find("WORD")[i].getAttribute("unicode");
            var nCode = 0;
            if (szHex != null && szHex.length > 0) {
                nCode = parseInt(szHex, 16);
            }

            if (DEF_UNICODE_BMP_EUDC_ONLY) {
                if (nCode < DEF_UNICODE_BMP_EUDC_BEGIN ||
                        nCode > DEF_UNICODE_BMP_EUDC_END) {
                    continue;
                }
            }

            if (DEF_FULL_CNS) {
                tUnicodes.push(nCode);
            } else {
                if (GetWordCodeType(nCode) != DEF_WORDTYPE_OTHER) {
                    tUnicodes.push(nCode);
                }
            }
        }
        tUnicodes.sort(SortCode);
    }
    return tUnicodes;
}

function SortCode(a, b) {
    return a - b;
}

function GetWordCodeType(pWordCode) {
    if (pWordCode >= 0xE000 && pWordCode <= DEF_UNICODE_BMP_EUDC_END) {
        return DEF_WORDTYPE_BMP_EUDC;
    } else if (pWordCode >= 0x20000 && pWordCode <= 0x2FFFF) {
        return DEF_WORDTYPE_PLANE2;
    } else if (pWordCode >= 0xF0000 && pWordCode <= 0xFFFFF) {
        return DEF_WORDTYPE_PLANE15;
    } else if (pWordCode >= 0x100000 && pWordCode <= 0x10FFFF) {
        return DEF_WORDTYPE_PLANE16;
    } else {
        return DEF_WORDTYPE_OTHER;
    }
}

function FilterImeKeyIn(pImeType, pNode) {
    if (event.keyCode == 37 || event.keyCode == 39) {
        return true;
    }

    if (pImeType == DEF_IME_ZUIN) {
        pNode.value = ImeMapping(pNode.value, DEF_IMEMAP_ZUIN, DEF_IMEMAPTYPE_DISPLAY);
    } else if (pImeType == DEF_IME_CHANGJEI) {
        pNode.value = ImeMapping(pNode.value, DEF_IMEMAP_CHANGJEI, DEF_IMEMAPTYPE_DISPLAY);
    }
}

function ImeMapping(pSrc, pMap, pMapType) {
    var szStr = "";
    var szChar = "";
    var i, j, nKeyIdx, nValueIdx;
    pSrc = pSrc.toUpperCase();

    if (pMapType == DEF_IMEMAPTYPE_QUERY) {
        nKeyIdx = 1;
        nValueIdx = 0;
    } else { // DEF_IMEMAPTYPE_DISPLAY or other
        nKeyIdx = 0;
        nValueIdx = 1;
    }

    for (i = 0; i < pSrc.length; i++) {
        szChar = pSrc.charAt(i);
        for (j = 0; j < pMap.length; j++) {
            if (szChar == pMap[j][nKeyIdx]) {
                szStr += pMap[j][nValueIdx];
                break;
            }
        }
        if (j >= pMap.length) {
            szStr += szChar;
        }
    }
    return szStr;
}

function SetCJQueryKey(Key) {
    document.getElementById('ChangJei').value += Key;
}

function SetZIQueryKey(Key) {
    document.getElementById('ZuIn').value += Key;
}

function CleanCJQueryKey() {
    document.getElementById('ChangJei').value = "";
}

function CleanZIQueryKey() {
    document.getElementById('ZuIn').value = "";
}
</script>
</head>

<body onload="InitPage()" bgcolor="#D4DFFF">

<div align="center">
<input type="button" value="上一頁" onclick="PrePage()">
<input type="button" value="下一頁" onclick="NextPage()">
總頁數：<font id="PageAmount" color="#0000FF"></font>頁。 到第 <select id="Goto" onchange="GotoPage(this.selectedIndex)"></select> 頁。
</div>
<table align="center" border="1" id="WordsTable" bordercolor="#0000CC" cellspacing="1" width="405">
	<tr>
		<td colspan="10" align="center"><b>【候選字】<span id="TotalWords"></span> </b></td>
	</tr>
	<tr>
		<td class="TD_PIC">&nbsp;</td><td class="TD_PIC">&nbsp;</td><td class="TD_PIC">&nbsp;</td><td class="TD_PIC">&nbsp;</td><td class="TD_PIC">&nbsp;</td>
		<td class="TD_PIC">&nbsp;</td><td class="TD_PIC">&nbsp;</td><td class="TD_PIC">&nbsp;</td><td class="TD_PIC">&nbsp;</td><td class="TD_PIC">&nbsp;</td>
	</tr>
	<tr>
		<td class="TD_PIC">&nbsp;</td><td class="TD_PIC">&nbsp;</td><td class="TD_PIC">&nbsp;</td><td class="TD_PIC">&nbsp;</td><td class="TD_PIC">&nbsp;</td>
		<td class="TD_PIC">&nbsp;</td><td class="TD_PIC">&nbsp;</td><td class="TD_PIC">&nbsp;</td><td class="TD_PIC">&nbsp;</td><td class="TD_PIC">&nbsp;</td>
	</tr>
</table>
<br />
<table align="center" border="1" bordercolor="#0000CC" cellspacing="1" width="405">
	<tr>
		<td align="center"><b>【注音查詢】</b>
            <input type="text" id="ZuIn" value="" size="10" readonly="readonly"/>
            <input type="button" value="查詢" onclick="Query(1)"/>
            <input type="button" value="清除" onclick="CleanZIQueryKey()"/>
		</td>
	</tr>
	<tr>
		<td align="center">
            <img border="0" src="<c:url value='/Astar/Image/buttonC8.jpg'/>" onclick="SetZIQueryKey('ㄅ')"/><img border="0" src="<c:url value='/Astar/Image/buttonD4.jpg'/>" onclick="SetZIQueryKey('ㄉ')"/><img border="0" src="<c:url value='/Astar/Image/buttonE3.jpg'/>" onclick="SetZIQueryKey('ˇ')"/><img border="0" src="<c:url value='/Astar/Image/buttonEF.jpg'/>" onclick="SetZIQueryKey('ˋ')"/><img border="0" src="<c:url value='/Astar/Image/buttonFB.jpg'/>" onclick="SetZIQueryKey('ㄓ')"/><img border="0" src="<c:url value='/Astar/Image/button107.jpg'/>" onclick="SetZIQueryKey('ˊ')"/><img border="0" src="<c:url value='/Astar/Image/button113.jpg'/>" onclick="SetZIQueryKey('˙')"/><img border="0" src="<c:url value='/Astar/Image/button11F.jpg'/>" onclick="SetZIQueryKey('ㄚ')"/><img border="0" src="<c:url value='/Astar/Image/button4.jpg'/>" onclick="SetZIQueryKey('ㄞ')"/><img border="0" src="<c:url value='/Astar/Image/button12B.jpg'/>" onclick="SetZIQueryKey('ㄢ')"/><img border="0" src="<c:url value='/Astar/Image/button137.jpg'/>" onclick="SetZIQueryKey('ㄦ')"/>
            <br />
            <img border="0" src="<c:url value='/Astar/Image/buttonCB.jpg'/>" onclick="SetZIQueryKey('ㄆ')"/><img border="0" src="<c:url value='/Astar/Image/buttonDA.jpg'/>" onclick="SetZIQueryKey('ㄊ')"/><img border="0" src="<c:url value='/Astar/Image/buttonE6.jpg'/>" onclick="SetZIQueryKey('ㄍ')"/><img border="0" src="<c:url value='/Astar/Image/buttonF2.jpg'/>" onclick="SetZIQueryKey('ㄐ')"/><img border="0" src="<c:url value='/Astar/Image/buttonFE.jpg'/>" onclick="SetZIQueryKey('ㄔ')"/><img border="0" src="<c:url value='/Astar/Image/button10A.jpg'/>" onclick="SetZIQueryKey('ㄗ')"/><img border="0" src="<c:url value='/Astar/Image/button116.jpg'/>" onclick="SetZIQueryKey('ㄧ')"/><img border="0" src="<c:url value='/Astar/Image/button122.jpg'/>" onclick="SetZIQueryKey('ㄛ')"/><img border="0" src="<c:url value='/Astar/Image/button7.jpg'/>" onclick="SetZIQueryKey('ㄟ')"/><img border="0" src="<c:url value='/Astar/Image/button12E.jpg'/>" onclick="SetZIQueryKey('ㄣ')"/>
            <br />
            <img border="0" src="<c:url value='/Astar/Image/buttonCE.jpg'/>" onclick="SetZIQueryKey('ㄇ')"/><img border="0" src="<c:url value='/Astar/Image/buttonDD.jpg'/>" onclick="SetZIQueryKey('ㄋ')"/><img border="0" src="<c:url value='/Astar/Image/buttonE9.jpg'/>" onclick="SetZIQueryKey('ㄎ')"/><img border="0" src="<c:url value='/Astar/Image/buttonF5.jpg'/>" onclick="SetZIQueryKey('ㄑ')"/><img border="0" src="<c:url value='/Astar/Image/button101.jpg'/>" onclick="SetZIQueryKey('ㄕ')"/><img border="0" src="<c:url value='/Astar/Image/button10D.jpg'/>" onclick="SetZIQueryKey('ㄘ')"/><img border="0" src="<c:url value='/Astar/Image/button119.jpg'/>" onclick="SetZIQueryKey('ㄨ')"/><img border="0" src="<c:url value='/Astar/Image/button125.jpg'/>" onclick="SetZIQueryKey('ㄜ')"/><img border="0" src="<c:url value='/Astar/Image/buttonA.jpg'/>" onclick="SetZIQueryKey('ㄠ')"/><img border="0" src="<c:url value='/Astar/Image/button131.jpg'/>" onclick="SetZIQueryKey('ㄤ')"/>
            <br />
            <img border="0" src="<c:url value='/Astar/Image/buttonD1.jpg'/>" onclick="SetZIQueryKey('ㄈ')"/><img border="0" src="<c:url value='/Astar/Image/buttonE0.jpg'/>" onclick="SetZIQueryKey('ㄌ')"/><img border="0" src="<c:url value='/Astar/Image/buttonEC.jpg'/>" onclick="SetZIQueryKey('ㄏ')"/><img border="0" src="<c:url value='/Astar/Image/buttonF8.jpg'/>" onclick="SetZIQueryKey('ㄒ')"/><img border="0" src="<c:url value='/Astar/Image/button104.jpg'/>" onclick="SetZIQueryKey('ㄖ')"/><img border="0" src="<c:url value='/Astar/Image/button110.jpg'/>" onclick="SetZIQueryKey('ㄙ')"/><img border="0" src="<c:url value='/Astar/Image/button11C.jpg'/>" onclick="SetZIQueryKey('ㄩ')"/><img border="0" src="<c:url value='/Astar/Image/button128.jpg'/>" onclick="SetZIQueryKey('ㄝ')"/><img border="0" src="<c:url value='/Astar/Image/buttonD.jpg'/>" onclick="SetZIQueryKey('ㄡ')"/><img border="0" src="<c:url value='/Astar/Image/button134.jpg'/>" onclick="SetZIQueryKey('ㄥ')"/>
        </td>
	</tr>
</table>
<br />
<table align="center" border="1" bordercolor="#0000CC" cellspacing="1" width="405">
	<tr>
		<td align="center"><b>【倉頡查詢】</b>
            <input type="text" id="ChangJei" value="" size="10" readonly="readonly"/>
            <input type="button" value="查詢" onclick="Query(2)"/>
            <input type="button" value="清除" onclick="CleanCJQueryKey()"/>
        </td>
	</tr>
	<tr>
		<td align="center">
            <img border="0" src="<c:url value='/Astar/Image/button53.jpg'/>" onclick="SetCJQueryKey('手')"/><img border="0" src="<c:url value='/Astar/Image/button7A.jpg'/>" onclick="SetCJQueryKey('田')"/><img border="0" src="<c:url value='/Astar/Image/button7D.jpg'/>" onclick="SetCJQueryKey('水')"/><img border="0" src="<c:url value='/Astar/Image/button80.jpg'/>" onclick="SetCJQueryKey('口')"/><img border="0" src="<c:url value='/Astar/Image/button83.jpg'/>" onclick="SetCJQueryKey('廿')"/><img border="0" src="<c:url value='/Astar/Image/button86.jpg'/>" onclick="SetCJQueryKey('卜')"/><img border="0" src="<c:url value='/Astar/Image/button89.jpg'/>" onclick="SetCJQueryKey('山')"/><img border="0" src="<c:url value='/Astar/Image/button8C.jpg'/>" onclick="SetCJQueryKey('戈')"/><img border="0" src="<c:url value='/Astar/Image/button8F.jpg'/>" onclick="SetCJQueryKey('人')"/><img border="0" src="<c:url value='/Astar/Image/button92.jpg'/>" onclick="SetCJQueryKey('心')"/>
            <br />
            <img border="0" src="<c:url value='/Astar/Image/button95.jpg'/>" onclick="SetCJQueryKey('日')"/><img border="0" src="<c:url value='/Astar/Image/button98.jpg'/>" onclick="SetCJQueryKey('尸')"/><img border="0" src="<c:url value='/Astar/Image/button9B.jpg'/>" onclick="SetCJQueryKey('木')"/><img border="0" src="<c:url value='/Astar/Image/button9E.jpg'/>" onclick="SetCJQueryKey('火')"/><img border="0" src="<c:url value='/Astar/Image/buttonA1.jpg'/>" onclick="SetCJQueryKey('土')"/><img border="0" src="<c:url value='/Astar/Image/buttonA4.jpg'/>" onclick="SetCJQueryKey('竹')"/><img border="0" src="<c:url value='/Astar/Image/buttonA7.jpg'/>" onclick="SetCJQueryKey('十')"/><img border="0" src="<c:url value='/Astar/Image/buttonAA.jpg'/>" onclick="SetCJQueryKey('大')"/><img border="0" src="<c:url value='/Astar/Image/buttonAD.jpg'/>" onclick="SetCJQueryKey('中')"/>
            <br />
            <img border="0" src="<c:url value='/Astar/Image/buttonB0.jpg'/>" onclick="SetCJQueryKey('重')"/><img border="0" src="<c:url value='/Astar/Image/buttonB3.jpg'/>" onclick="SetCJQueryKey('難')"/><img border="0" src="<c:url value='/Astar/Image/buttonB6.jpg'/>" onclick="SetCJQueryKey('金')"/><img border="0" src="<c:url value='/Astar/Image/buttonB9.jpg'/>" onclick="SetCJQueryKey('女')"/><img border="0" src="<c:url value='/Astar/Image/buttonBC.jpg'/>" onclick="SetCJQueryKey('月')"/><img border="0" src="<c:url value='/Astar/Image/buttonBF.jpg'/>" onclick="SetCJQueryKey('弓')"/><img border="0" src="<c:url value='/Astar/Image/buttonC2.jpg'/>" onclick="SetCJQueryKey('一')"/>
        </td>
	</tr>
</table>

</body>

</html>
