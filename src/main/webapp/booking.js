let authorizationFailCount = 0;
document.getElementById("bookForm").addEventListener("submit",(e)=>{
    e.preventDefault();
    const formdata = new FormData(e.target);
    const params = new URLSearchParams(formdata).toString();
    console.log(params);
    const url = "/railwayProject/webapi/root/book-ticket";
    fetch(url,{
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
          'Authorization': 'Bearer '+window.sessionStorage.getItem("token")
        },
        body: params,
      })
      .then((response)=>{
        if(response.status==401){
            console.log("Authorization failed\n");
            if(authorizationFailCount==5){
                window.sessionStorage.removeItemItem("token");
                alert("Please log in again");
            }
            authorizationFailCount++;
        }
        if(response.status!=200){
            console.log(response);
            return null;
        }
        return response.json();
    }).then((value)=>{
        console.log(value);
        if(value.status=="success"){
            alert("Booking Successful");
            window.location.href="/railwayProject/index.html";
        }
    }).catch((err)=>{
        console.log(err);
    });
});

function prepareBookingForm(){
    const url = new URL(window.location.href);
    document.getElementById("from").value = url.searchParams.get("from");
    document.getElementById("to").value = url.searchParams.get("to");
    document.getElementById("train_no").value = url.searchParams.get("tno");
    document.getElementById("train_name").value = url.searchParams.get("tname");
}

function isLoggedin(){
    const token = window.sessionStorage.getItem("token");
    if(token==null){
        return false;
    }
    return true;
}

document.getElementById("loginButton").addEventListener("click",(e)=>{
     
    window.location.href ="/railwayProject/login.html";
});

document.getElementById("signupButton").addEventListener("click",(e)=>{
    window.location.href ="/railwayProject/signup.html";
});

/*if(!isLoggedin()){
    alert("Please log in to Book Ticket\n");
}*/
//prepareBookingForm();


function testing(){
    const obj = {
        train_no:'29939',
        from:'HWH',
        to:'CTK',
        passangers:[
            {
                p_name:'Sayan',
                adhaar:'70220093'
            },
            {
                p_name:'Anish',
                adhaar:'87298093'
            },
            {
                p_name:'Bijoy',
                adhaar:'10220093'
            }
        ] 
    }
    let bodystring = JSON.stringify(obj);
    console.log(bodystring) 
    const url = "/railwayProject/webapi/root/train-booking"
    fetch(url,{
        method:'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: bodystring
    }).then((response)=>{
        if(response.status!=200){
            console.log(response.status);
        }
        return response.json();
    }).then((value)=>{
        console.log(value);
    }).catch(console.log);
}
