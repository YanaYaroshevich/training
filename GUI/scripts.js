var name = "";

function run(){
	serverCheck(true);

	var appContainer = document.getElementsByClassName('inputName')[0];
	appContainer.addEventListener('click', delegateEvent);

	appContainer = document.getElementsByClassName('inputMsg')[0];
	appContainer.addEventListener('click', delegateEvent);
}

function delegateEvent(evtObj) {
	if(evtObj.type === 'click'){
		if (evtObj.target.classList.contains('btn-success') || evtObj.target.classList.contains('btn-info'))
			onInputNameButtonClick(evtObj);
		else if (evtObj.target.classList.contains('btn-primary'))
			onInputMsgButtonClick(evtObj);
	}
}

function onInputMsgButtonClick(evtObj){
	var textField = document.getElementById('inputMsgText');
	sendMsg(textField.value, evtObj);
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

function createGreeting(value){
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

function createMsg(value){
	var userMessage = document.createElement('div');
	var attr = document.createAttribute("class");
	attr.value = "userMessage";
	userMessage.setAttributeNode(attr);

	var userName = document.createElement('div');
	attr = document.createAttribute("class");
	attr.value = "userName";
	userName.setAttributeNode(attr);
	userName.innerHTML = name;

	var text = document.createElement('div');
	attr = document.createAttribute("class");
	attr.value = "text";
	text.setAttributeNode(attr);
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
	var greeting = createGreeting(name);

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

function sendMsg(value, evtObj){
	if(!value){
		return;
	}

	var items = document.getElementsByClassName('history')[0];
	var userMessage = createMsg(value);
	items.appendChild(userMessage);
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