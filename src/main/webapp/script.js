const URL = "/railwayProject/webapi/root";
const list = document.getElementById("train-lists");
let trains;
let boarding;
let deboarding;

document.getElementById("searchForm").addEventListener("submit",(e)=>{
    e.preventDefault();
    list.innerHTML = "<p>Just a second...</p>"
    const searchform = e.target; 
    const from = searchform._from.value.trim().toUpperCase();
    const to = searchform._to.value.trim().toUpperCase();

    if(from.length<3||from.length>5){
        alert("Invalid Station Code")
    }

    if(to.length<3||to.length>5){
        alert("Invalid Station Code")
    }

    let url = URL +"/route"+ "?"+"from="+from+"&"+"to"+"="+to;
    boarding = from;
    deboarding = to;
    fetch(url,{
        method:'GET',
        headers:{
            Authorization: 'Bearer '+sessionStorage.getItem("token")
        }
    })
    .then(response => {
        if(response.status!=200){
            return null;
        }
        return response.json();
    }).then( value => {
        trains = value.train_list;
        addTrains(value.train_list,from,to);
    }).catch((err)=>{
        console.log(err);
        list.innerHTML = "<p>OOps!.. this was not supposed to happen</p>"
    });
});

function addTrains(trainlist,from,to){
    console.log("add trains invoked");
    console.log(trainlist.length);
    
    if(trainlist.length==0){
        list.innerHTML = "<p>Sorry! no train on this route</p>";
        return;
    }
    list.innerHTML = "<tr><th>Train Number</th><th>Train Name</th><th>Available Seat</th><th>Booking</th></tr>"
    for(let i=0;i<trainlist.length;i++){
        const row = document.createElement("tr");
        let data1 = document.createElement("td");
        data1.innerText = trainlist[i].train_number;
        let data2 = document.createElement("td");
        data2.innerText = trainlist[i].train_name;
        let data3 = document.createElement("td");
        data3.innerText = trainlist[i].available;
        row.appendChild(data1);
        row.appendChild(data2);
        row.appendChild(data3);
        let data4 = document.createElement("td");
        let bookingButton = document.createElement("button");
        bookingButton.innerText = "Book Now"
        bookingButton.addEventListener('click',()=>{
            const query = `?tno=${trainlist[i].train_number}&tname=${trainlist[i].train_name}&from=${from}&to=${to}`;
            const url ="/railwayProject/booking.html"+query;
            window.location.href = url;
        });
        bookingButton.setAttribute("value","Book Now");
        data4.appendChild(bookingButton);
        row.appendChild(data4);
        list.appendChild(row);
    }
}

document.getElementById("swapButton").addEventListener('click',function (event){
    event.preventDefault();
    const from = document.getElementById("_from").value;
    const to = document.getElementById("_to").value;
    console.log(from);
    console.log(to);
    document.getElementById("_from").value = to;
    document.getElementById("_to").value = from;
});



document.getElementById("loginButton").addEventListener("click",(e)=>{
    window.location.href ="/railwayProject/login.html";
})

document.getElementById("signupButton").addEventListener("click",(e)=>{
    window.location.href ="/railwayProject/signup.html";
})
