var name;

function run(){
	serverCheck(false);
	var appContainer = document.getElementsByClassName('inputName')[0];
	appContainer.addEventListener('click', delegateEvent);
}

function delegateEvent(evtObj) {
	if(evtObj.type === 'click')
		if (evtObj.target.classList.contains('btn-success') || evtObj.target.classList.contains('btn-info'))
			onInputNameButtonClick(evtObj);
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
		document.getElementById("inputMsg").style.visibility = "visible";
	}

	document.getElementById("form1").style.visibility = "hidden";
	document.getElementById("form2").style.visibility = "visible";
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