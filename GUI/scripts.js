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
			if (editFlag == false)
				onInputMsgButtonClick(evtObj);
			else
				onSendEditedMsgBtnClick(evtObj);
		}
		else if (evtObj.target.classList.contains('btn-default') || evtObj.target.classList.contains('glyphicon')){
			onEditMsgButtonClick(evtObj);
		}
	}
}

function onInputMsgButtonClick(evtObj){
	var textField = document.getElementById('inputMsgText');
	sendMsg(textField.value, evtObj);
	textField.value = '';
}

function onSendEditedMsgBtnClick(evtObj){
	var textField = document.getElementById('inputMsgText');
	sendEditedMsg(textField.value, evtObj);
	textField.value = '';
}

function onInputNameButtonClick(evtObj){
	var nameField;

	if(evtObj.target.classList.contains('btn-success')){
		nameField = document.getElementById('nameInputText');
	}

	else if(evtObj.target.classList.contains('btn-info')){
		nameField = document.getElementById('nameChangeText');
	}

	setName(nameField.value, evtObj);
	nameField.value = '';
}

function onEditMsgButtonClick(evtObj){
	if (evtObj.target.hasChildNodes()){
		if (evtObj.target.firstElementChild.className == "glyphicon glyphicon-wrench"){
			var children = evtObj.target.parentElement.childNodes;
			editMsg(evtObj, children, evtObj.target.parentElement);
			
		}
		else if (evtObj.target.firstElementChild.className == "glyphicon glyphicon-trash"){
			var items = document.getElementsByClassName('history')[0];
			items.removeChild(evtObj.target.parentElement);	
		}
	}
	else {
		if (evtObj.target.className == "glyphicon glyphicon-wrench"){
			var children = evtObj.target.parentElement.parentElement.childNodes;
			editMsg(evtObj, children, evtObj.target.parentElement.parentElement);
		}
		else if (evtObj.target.className == "glyphicon glyphicon-trash"){
			var items = document.getElementsByClassName('history')[0];
			items.removeChild(evtObj.target.parentElement.parentElement);	
		}
	}
}

function greetingCreation(value){
	var greeting = document.createElement('h3');
	greeting.innerHTML = 'Hello, ' + name + '!';
	return greeting; 
}

function createBtn(btnClass){
	var btn = document.createElement('button');
	var attr = document.createAttribute('type');
	attr.value = "button";
	btn.setAttributeNode(attr);
	
	attr = document.createAttribute('class');
	attr.value = "btn btn-default btn-sm";
	btn.setAttributeNode(attr);

	var sp = document.createElement('span');
	attr = document.createAttribute("class");
	attr.value = btnClass;
	sp.setAttributeNode(attr);

	attr = document.createAttribute('style');
	attr.value = "color:red;";
	sp.setAttributeNode(attr);

	btn.appendChild(sp);
	return btn;
}

function childCreation(value){
	var child = document.createElement('div');
	var attr = document.createAttribute("class");
	attr.value = value;
	child.setAttributeNode(attr);
	return child;
}

function createMsg(value){
	var userMessage = childCreation("userMessage");

	var userName = childCreation("userName");
	userName.innerHTML = name;

	var text = childCreation("text");
	text.innerHTML = value;

	var delBtn = createBtn("glyphicon glyphicon-wrench");
	var editBtn = createBtn("glyphicon glyphicon-trash");
	
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