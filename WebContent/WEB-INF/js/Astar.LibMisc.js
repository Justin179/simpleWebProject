/*
Title: Astar.LibMisc
Description: Astar.LibMisc
Company: Astar Printerlink Co., Ltd.
Product: Astar.LibMisc
Copyright: Copyright ©  2012
Version: 2.13.4.20
Comment: The encoding of this file must be UTF-8.
*/
function FilterNumberKeyIn(IsHex) {
    var bIsNumber = false;
    var nCode = window.event.keyCode;
    if (nCode >= 0x30 && nCode <= 0x39) {
        bIsNumber = true;
    } else if (IsHex && ((nCode >= 0x41 && nCode <= 0x46) || (nCode >= 0x61 && nCode <= 0x66))) {
        bIsNumber = true;
    }

    if (!bIsNumber) {
        window.event.keyCode = 0;
    }
}

function MergeQueryString(SrcStr, Name, Value) {
    if (IsNullOrEmptyString(SrcStr)) {
        SrcStr = "?";
    } else {
        SrcStr += "&";
    }
    return SrcStr + Name + "=" + encodeURIComponent(Value);
}

function IsNullOrEmptyString(Value) {
    return (Value == null || Value.length == 0 || Value.toUpperCase() == "NULL");
}

function TrimString(Value) {
    return Value.replace(/^\s+|\s+$/g, '');
}

function GetQueryStringParameter(Name) {
    var szValue = decodeURI((RegExp(Name + '=' + '(.+?)(&|$)').exec(location.search) || [, null])[1]);
    return (szValue == "null") ? "" : szValue;
}
// IE only --- begin
function GetServerXmlDoccument(URL) {
    var tXDoc = new ActiveXObject("Microsoft.XMLDOM");
    tXDoc.async = false;
    if (!tXDoc.load(URL)) {
        tXDoc = null;
    }
    return tXDoc;
}

function GetXmlNodeString(XDoc, XPath, Default) {
    if (XDoc != null && !IsNullOrEmptyString(XPath)) {
        var tXNode = XDoc.documentElement.selectSingleNode(XPath);
        if (tXNode != null && !IsNullOrEmptyString(tXNode.text)) {
            return tXNode.text;
        }
    }
    return Default;
}

function GetXmlNodeNumber(XDoc, XPath, Default) {
    var szString = GetXmlNodeString(XDoc, XPath, "");
    if (!IsNullOrEmptyString(szString)) {
        var nNumber = parseInt(szString, 10);
        if (!isNaN(nNumber)) {
            return nNumber;
        }
    }
    return Default;
}

function GetAllNodes(XDoc, XPath) {
    if (XDoc != null && !IsNullOrEmptyString(XPath)) {
        var tNodes = XDoc.documentElement.selectNodes(XPath);
        if (tNodes != null) {
            return tNodes;
        }
    }
    return null;
}

function GetXmlNodeAttrString(XNode, AttrName, Default) {
    if (XNode != null && !IsNullOrEmptyString(AttrName)) {
        for (var i = 0; i < XNode.attributes.length; i++) {
            if (XNode.attributes[i].name == AttrName) {
                if (!IsNullOrEmptyString(XNode.attributes[i].value)) {
                    return XNode.attributes[i].value;
                }
            }
        }
    }
    return Default;
}

function GetXmlNodeAttrNumber(XNode, AttrName, Default) {
    return parseInt(GetXmlNodeAttrString(XNode, AttrName, Default.toString()));
}
// IE only --- end
function GetServerXmlSync(URL) {
    var http_request = false;

    if (window.XMLHttpRequest) { // Mozilla, Safari,...
        http_request = new XMLHttpRequest();
    } else if (window.ActiveXObject) { // IE
        try {
            http_request = new ActiveXObject("Msxml2.XMLHTTP");
        } catch (e) {
            try {
                http_request = new ActiveXObject("Microsoft.XMLHTTP");
            } catch (e) { }
        }
    }

    if (!http_request) {
        alert('Cannot create an XMLHTTP instance');
        return "";
    }
    http_request.open('GET', URL, false);
    http_request.send(null);
    if (http_request.status == 200) {
        return http_request.responseText;
    } else {
        alert('Server response error code ' + http_request.status);
        return "";
    }
}

var gResultHandler = null;
function doAjax(pUrl, pResultHandler) {
    gResultHandler = pResultHandler;
    makeRequest(pUrl);
}

function makeRequest(url) {
    var http_request = false;

    if (window.XMLHttpRequest) { // Mozilla, Safari,...
        http_request = new XMLHttpRequest();
    } else if (window.ActiveXObject) { // IE
        try {
            http_request = new ActiveXObject("Msxml2.XMLHTTP");
        } catch (e) {
            try {
                http_request = new ActiveXObject("Microsoft.XMLHTTP");
            } catch (e) { }
        }
    }

    if (!http_request) {
        alert('Giving up :( Cannot create an XMLHTTP instance');
        return false;
    }
    // 定義事件處理函數為 alterContents()
    http_request.onreadystatechange = function () {
        alertContents(http_request);
    };
    http_request.open('GET', url, true);
    http_request.send(null);
}

function alertContents(http_request) {
    if (http_request.readyState == 4) {
        if (http_request.status == 200) {
            var response = http_request.responseText;
            if (null != gResultHandler) {
                gResultHandler(response);
            }
        } else if (http_request.status == 403) {
            alert('無執行權限');
        } else {
            alert('Request failed, StateCode:' + http_request.status);
        }
    }
}