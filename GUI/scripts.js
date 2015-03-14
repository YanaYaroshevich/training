'use strict'

var name = "";
var editFlag = false;
var editMsgPos = -1;

function run(){
	serverCheck(true);
	document.addEventListener('click', delegateEvent);
}

function delegateEvent(evtObj) {
	if(evtObj.type === 'click'){
		if (evtObj.target.classList.contains('btn-success') || evtObj.target.classList.contains('btn-info'))
			onInputNameButtonClick(evtObj);
		else if (evtObj.target.classList.contains('btn-primary')){
			onSendMsgButtonClick(evtObj);
		}
		else if (evtObj.target.classList.contains('btn-default') || evtObj.target.classList.contains('glyphicon')){
			onEditMsgButtonClick(evtObj);
		}
	}
}

function onSendMsgButtonClick(evtObj){
	var textField = document.getElementById('inputMsgText');
	(editFlag) ? sendEditedMsg(textField.value, evtObj) : sendMsg(textField.value, evtObj);
	textField.value = '';
}

function onInputNameButtonClick(evtObj){
	var nameField;
	nameField = (evtObj.target.classList.contains('btn-success')) ? 
				document.getElementById('nameInputText') : document.getElementById('nameChangeText'); 
	setName(nameField.value, evtObj);
	nameField.value = '';
}

function onEditMsgButtonClick(evtObj){
	var items = document.getElementsByClassName('history')[0]; 
	var msg = (evtObj.target.hasChildNodes()) ? evtObj.target.parentElement : evtObj.target.parentElement.parentElement;
	var children = msg.childNodes;
	var indicator = (evtObj.target.hasChildNodes()) ? evtObj.target.firstElementChild.className : evtObj.target.className;
	(indicator == "glyphicon glyphicon-wrench") ? editMsg(evtObj, children, msg) : items.removeChild(msg);
}

function setAttr(obj, attrType, attrValue){
	var attr = document.createAttribute(attrType);
	attr.value = attrValue;
	obj.setAttributeNode(attr);
}

function greetingCreation(value){
	var greeting = document.createElement('h3');
	greeting.innerHTML = 'Hello, ' + name + '!';
	return greeting; 
}

function btnCreation(btnClass){
	var btn = document.createElement('button');
	setAttr(btn, 'type', 'button');
	setAttr(btn, 'class', 'btn btn-default btn-sm');

	var sp = document.createElement('span');
	setAttr(sp, 'class', btnClass);
	setAttr(sp, 'style', 'color:#003264;');

	btn.appendChild(sp);
	return btn;
}

function childCreation(value, elType){
	var child = document.createElement(elType);
	setAttr(child, 'class', value);
	return child;
}

function createMsg(value){
	var userMessage = childCreation("userMessage", 'div');

	var userName = childCreation("userName", 'div');
	userName.innerHTML = name;

	var text = childCreation("text", 'pre');
	text.innerHTML = value;

	var delBtn = btnCreation("glyphicon glyphicon-wrench");
	var editBtn = btnCreation("glyphicon glyphicon-trash");
	
	userMessage.appendChild(userName);
	userMessage.appendChild(text);
	userMessage.appendChild(delBtn);
	userMessage.appendChild(editBtn);

	return userMessage;
}

function setName(value, evtObj){
	if(!value){
		return;
	}

	var prevName = name;
	name = value;
	var items = document.getElementsByClassName('inputName')[0];
	var greeting = greetingCreation(name);

	if(evtObj.target.classList.contains('btn-info')){
		var h3 = document.getElementsByTagName('h3')[0];
		h3.innerHTML = 'Hello, ' + name + '!';
	}

	else{
		items.appendChild(greeting);
		document.getElementById("sendBtn").style.visibility = "visible";
		document.getElementById("coolMan").style.visibility = "visible";
		document.getElementById("inputMsgText").style.visibility = "visible";
	}

	document.getElementById("form1").style.visibility = "hidden";
	document.getElementById("form2").style.visibility = "visible";
}

function editMsg(evtObj, children, msg){
	var textToChange = '';
	for (var i = 0; i < children.length; i++){
		if(children[i].className == "text"){
			textToChange = children[i].innerHTML;
			break;
		}
	}
	var field = document.getElementById("inputMsgText");
	field.value = textToChange;

	editFlag = true;
	var items = document.getElementsByClassName('history')[0];
	for (var i = 0; i < items.childNodes.length; i++){
		if (items.childNodes[i] == msg){
			editMsgPos = i;
			break;
		}
	}	
}

function sendMsg(value, evtObj){
	if(!value){
		return;
	}
	var items = document.getElementsByClassName('history')[0];
	var userMessage = createMsg(value);
	items.appendChild(userMessage);
}

function sendEditedMsg(value, evtObj){
	var items = document.getElementsByClassName('history')[0];
	var userMessage = createMsg(value);
	items.replaceChild(userMessage, items.childNodes[editMsgPos]);
	editFlag = false;
}

function serverCheck(flag){
	if (flag){
		document.getElementById("greenLamp").style.visibility = "visible";
		document.getElementById("redLamp").style.visibility = "hidden";
	}
	else{
		document.getElementById("greenLamp").style.visibility = "hidden";
		document.getElementById("redLamp").style.visibility = "visible";
	}
}