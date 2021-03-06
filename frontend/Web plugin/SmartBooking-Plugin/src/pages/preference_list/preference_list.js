import UserController from '../login/UserController.js';

var loadedHotelList = [];

const loginstate = window.localStorage.getItem('loginstate');

async function toggleSaveBtn(toggle){
    if (toggle) {
        document.getElementById('save-list').classList.remove("hidden");
        document.getElementById('log-to-save').classList.add('hidden');
    } else {
        document.getElementById('save-list').classList.add("hidden");
        document.getElementById('log-to-save').classList.remove('hidden');
    }
}

if (loginstate == "true") {
    toggleSaveBtn(true);
    let response = await UserController.getUserHotels();
    if (response.succes) {
        loadedHotelList = response.message;
    } else {
    }
} else {
    toggleSaveBtn(false);
    loadedHotelList = [];
}

export function checkCheckbox(listElem) {
    listElem.children[3].setAttribute("checked", "checked");
}

function uncheckCheckbox(listElem) {
    listElem.children[3].removeAttribute("checked");
}

function getListElem(element = "hotel name", adress = "hotel adress", link = "#") {
    let hotel_list_elem = document.createElement("div");
    hotel_list_elem.classList.add("b-contain");
    let list_elem =
        `<span class="remove">X</span>
    <a class="hotel-link" href=${link} target="_blank" rel="noopener"><span class="hotel-name">${element}</span></a>
	<span class="hotel-adress" style="display:none">${adress}</span>
	<input type="checkbox" onclick="return false">
<div class="b-input"></div>`

    hotel_list_elem.innerHTML = list_elem;
    return hotel_list_elem;
}

var hotelList = [];

function addUserHotelList() {
    chrome.storage.sync.get('locations', handleUserHotelsLoad);
}
addUserHotelList();

async function handleUserHotelsLoad(value) {
    var control_group = document.getElementById("control-group");
    control_group.innerHTML = "Nothing here yet";

    if (loadedHotelList.length > 0)
        control_group.innerHTML = "";

    loadedHotelList.forEach(element => {
        let hotel_list_elem = getListElem(element.hotelName, element.locationName, element.hotelUrl);
        checkCheckbox(hotel_list_elem);

        control_group.append(hotel_list_elem);
    });

    hotelList = value.locations;

    if (hotelList) {
        if (hotelList.length > 0 && loadedHotelList.length == 0)
            control_group.innerHTML = "";

        hotelList.forEach(element => {
            let hotel_link = "https://www.booking.com" + element.hotelPath;

            if (!loadedHotelList.some(function (loaded) { return loaded.hotelUrl === hotel_link; })) {
                let hotel_list_elem = getListElem(element.hotelName, element.hotelLocation, hotel_link);
                control_group.append(hotel_list_elem);
            }
        });
    }

    addRemoveButton();
}

function addRemoveButton() {
    var remove = document.getElementsByClassName("remove");
    for (var i = 0; i < remove.length; i++) {
        remove[i].addEventListener('click', (el) => {
            showConfirmBox("Are you sure you want to remove this hotel from your favorites?", () => {
                var label = el.target.parentElement;
                label.style.display = "none";
                removeLocation(label.getElementsByClassName("hotel-link")[0].href);
                label.remove();
            });
        });
    }
}

const asyncFilter = async (arr, predicate, url) => 
	arr.reduce(async (memo, e) =>
		await predicate(e, url) ? [...await memo, e] : memo
	, []);

async function tryToDeleteUserHotel(element, url) {
    if (element.hotelUrl === url) {
        let response = await UserController.deleteUserHotel(element.id);
        if (response.succes)
            return false;

        console.log(response.message);
    }
    return true;
}

async function removeLocation(url) {
    
    loadedHotelList = await asyncFilter(loadedHotelList, tryToDeleteUserHotel, url);
    chrome.storage.sync.get('locations', (value) => {
        console.log(loadedHotelList);
        let hotelList = value.locations;

        for (let i = 0; i < hotelList.length; i++) {
            if (("https://www.booking.com" + hotelList[i].hotelPath) == url) {
                hotelList.splice(i, 1);
            }
        }
        chrome.storage.sync.set({ "locations": hotelList });

        console.log(hotelList);

        if (hotelList.length == 0 && loadedHotelList.length == 0) {
            document.getElementById("control-group").innerText = "Nothing here yet";
        }
    });
}

document.getElementById("log-to-save").addEventListener('click', () => {
    window.location.href = "/src/pages/login/login.html";
});