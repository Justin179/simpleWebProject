//var DEF_DATA_WEB_PREFIX = "http://tnsweb.bot.com.tw"; 
var DEF_DATA_WEB_PREFIX = "/bottb/Astar";

function AstarWebEditor(TextBoxId) {
	this.id = TextBoxId;
	this.CodeMappings = new Array();
	this.Count = 0; // Count of used Values
	this.OnlyEUDC = false;
	this.WordToHex = false;
	
	// init object with server config
	var szXml = GetServerXmlSync(DEF_DATA_WEB_PREFIX + "/GetConfig.aspx");
	var tXDoc = $.parseXML(szXml);
	$xml = $(tXDoc);

	var tXObj = $xml.find("OnlyEUDC");
	if (tXObj != null && tXObj.length > 0) {
		this.OnlyEUDC = parseInt(tXObj.text()) == 1;
	}

	tXObj = $xml.find("WordToHex");
	if (tXObj != null && tXObj.length > 0) {
		this.WordToHex = parseInt(tXObj.text()) == 1;
	}

	var nLength = $xml.find("EUDC").length;
	for (var i = 0; i < nLength; i++) {
		var szHex = $xml.find("EUDC")[i].getAttribute("unicode");
		var tCodeMapping = new CodeMapping();
		tCodeMapping.EUDCode = parseInt(szHex, 16);
		tCodeMapping.WordCode = 0;
		this.CodeMappings.push(tCodeMapping);
	}

	this.SetCodeMapping = function(WordCode) {
		for (var i = 0; i < this.Count; i++) {
			if (WordCode == this.CodeMappings[i].WordCode) { // Code exist
				return this.CodeMappings[i].EUDCode;
			}
		}

		if (this.OnlyEUDC) {
			if (this.CodeMappings[this.Count] == null) {
				this.CodeMappings[this.Count] = new CodeMapping();
			}
			this.CodeMappings[this.Count].WordCode = WordCode;
			this.CodeMappings[this.Count].EUDCode = WordCode;
		} else {
			this.CodeMappings[this.Count].WordCode = WordCode;
		}
		this.Count++;
		return this.CodeMappings[this.Count - 1].EUDCode;
	};
	this.GetCodeMapping = function(EUDCode) {
		for (var i = 0; i < this.Count; i++) {
			if (EUDCode == this.CodeMappings[i].EUDCode) {
				return this.CodeMappings[i].WordCode;
			}
		}
		return -1;
	};
	this.GetAllCodeMappings = function() {
		var tMappings = new Array();
		for (var i = 0; i < this.Count; i++) {
			tMappings.push(this.CodeMappings[i]);
		}
		return tMappings;
	};

	var tNode = document.getElementById(this.id);
	if (tNode.value.length > 0) {
		for (var i = 0; i < tNode.value.length; i++) {
			var nWordCode = tNode.value.charCodeAt(i);
			if (nWordCode >= 0xE000 && (nWordCode <= 0xE000 + 6400)) {
				this.SetCodeMapping(nWordCode);
			}
		}

		ReloadWebFont(tNode, this.GetAllCodeMappings());
	}
}

function CodeMapping() {
	this.EUDCode = 0;
	this.WordCode = 0;
}

function CreateFontFace(FontFamily, TTFSrc, EOTSrc) {
	var szFontFace = "@font-face{";
	szFontFace += "font-family:" + FontFamily + ";";
	szFontFace += "font-style:normal;";
	szFontFace += "font-weight:normal;";
	szFontFace += "src:url('" + EOTSrc + "');";
	szFontFace += "src:url('" + EOTSrc + "?#iefix') format('embedded-opentype'),";
	szFontFace += "    url('" + TTFSrc + "') format('truetype');";
	szFontFace += "}";
	return szFontFace;
}

function AppendDocumetFontFace(FontFamily, TTFSrc, EOTSrc) {
	var szFontFace = CreateFontFace(FontFamily, TTFSrc, EOTSrc);
	var tNewStyle = document.createElement('style');
	$("<style type=\"text/css\">" + szFontFace + "</style>").appendTo("head");
}

function OpenAstarIME(HTMLPage, EditorId) {
	var szURL = HTMLPage + "?EditorId=" + encodeURIComponent(EditorId);
	var szStyle = "width=470";
	szStyle += ",height=490";
	szStyle += ",location=no";
	szStyle += ",menubar=no";
	szStyle += ",resizable=yes";
	szStyle += ",scrollbars=no";
	szStyle += ",status=no";
	szStyle += ",titlebar=no";
	szStyle += ",toolbar=no";
	window.open(szURL, "_blank", szStyle);
}

function ReloadWebFont(CurrentNode, CodeMappings) {
	var szWordCodes = "";
	var szEUDCodes = "";
	for (var i = 0; i < CodeMappings.length; i++) {
		szWordCodes += CodeMappings[i].WordCode.toString(16).toUpperCase()
				+ ",";
		szEUDCodes += CodeMappings[i].EUDCode.toString(16).toUpperCase() + ",";
	}
	szWordCodes = szWordCodes.substring(0, szWordCodes.length - 1);
	szEUDCodes = szEUDCodes.substring(0, szEUDCodes.length - 1);

	var szFontFamily = GetFontFamily();
	var szQueryString = "?WordCodes=" + encodeURIComponent(szWordCodes)
			+ "&EUDCodes=" + encodeURIComponent(szEUDCodes);
	var szTTFSrc = DEF_DATA_WEB_PREFIX + "/GetFontFile.aspx" + szQueryString;
	var szEOTSrc = DEF_DATA_WEB_PREFIX + "/GetFontFileEOT.aspx" + szQueryString;
	AppendDocumetFontFace(szFontFamily, szTTFSrc, szEOTSrc);

	var szAllFontFamilies = GetAllFontFamilies($("#" + CurrentNode.id).css(
			"font-family"), szFontFamily);
	$("#" + CurrentNode.id).css("font-family", szAllFontFamilies);
}

var DEF_FONT_FAMILY_PREFIX = "AstarWebFont-";
function GetFontFamily() {
	return DEF_FONT_FAMILY_PREFIX
			+ (new Date().getTime().toString(16).toUpperCase());
}

function GetAllFontFamilies(SrcFamily, NewFamily) {
	var tSrcFontArray = SrcFamily.split(',');
	var tNewFontArray = new Array();
	for (var i = 0; i < tSrcFontArray.length; i++) {
		if (tSrcFontArray[i].indexOf(DEF_FONT_FAMILY_PREFIX, 0) < 0) {
			tNewFontArray.push(tSrcFontArray[i]);
		}
	}
	tNewFontArray.push(NewFamily);
	return tNewFontArray.toString();
}

function ConvertBeforeSubmit(WebEditors, TextBoxId) {
	var szSrcValue = $("#" + TextBoxId).val();
	var szDestValue = "";
	var nCode = 0;
	var szChar = "";
	var tEditor = null;

	for (var i = 0; i < WebEditors.length; i++) {
		if (WebEditors[i].id == TextBoxId) {
			tEditor = WebEditors[i];
			break;
		}
	}

	if (tEditor == null) {
		alert("Invalid textbox id");
		return;
	}

	for (var i = 0; i < szSrcValue.length; i++) {
		nCode = tEditor.GetCodeMapping(szSrcValue.charCodeAt(i));
		if (nCode <= 0) {
			nCode = szSrcValue.charCodeAt(i);
		}
		szChar = UTF16Encode(nCode);
		szDestValue += szChar;
	}
	$("#" + TextBoxId).val(szDestValue);
}

function ConvertHexBeforeSubmit(WebEditors, TextBoxId, HiddenId) {
	var szSrcValue = $("#" + TextBoxId).val();
	var szDestValue = "";
	var nCode = 0;
	var szChar = "";
	var tEditor = null;

	for (var i = 0; i < WebEditors.length; i++) {
		if (WebEditors[i].id == TextBoxId) {
			tEditor = WebEditors[i];
			break;
		}
	}

	if (tEditor == null) {
		alert("Invalid textbox id");
		return;
	}

	for (var i = 0; i < szSrcValue.length; i++) {
		nCode = tEditor.GetCodeMapping(szSrcValue.charCodeAt(i));
		if (nCode <= 0) {
			nCode = szSrcValue.charCodeAt(i);
		}
		szChar = UTF16Encode(nCode);
		if (tEditor.WordToHex) {
			szChar = WordToHex(szChar);
		}
		szDestValue += szChar;
	}
	$("#" + HiddenId).val(szDestValue);
}

function UTF16Encode(pUnicode) {
	var t_text = new Array(2);
	if (pUnicode >= 0x10000) {
		t_text[0] = (0xD800 | ((pUnicode - 0x10000) >> 10));
		t_text[1] = (0xDC00 | ((pUnicode - 0x10000) & 0x3FF));
		return String.fromCharCode(t_text[0], t_text[1]);
	} else {
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