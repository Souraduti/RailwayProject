function suggestionHandler(e){
    const query = e.target.value.trim();
    if (query.length > 0) {
        let suggestions = fetchSuggestions(query,e.target);
    } else {
        clearSuggestions();
    }
}

function fetchSuggestions(query,target){
    let url = "/railwayProject/webapi/root/suggestion?query="+query;
    let suggestion = fetch(url,
        {
        method:'GET',
        headers:{
            Authorization: 'Bearer '+sessionStorage.getItem("token")
        }
    })
    .then((response)=>{
        if(response.status!=200){
            return null;
        }
        return response.json();
    })
    .then((value)=>{
        displaySuggestions(value,target);
    })
    .catch(console.log);
}

function displaySuggestions(suggestions,target) {
    let suggestionslistId;
    if(target.id==="_from"){
        suggestionslistId = "suggestionsFrom";
    }else{
        suggestionslistId = "suggestionsTo";
    }
    const suggestionsContainer = document.getElementById(suggestionslistId);
    clearSuggestions();
    suggestions.suggestion_list.forEach(suggestion => {
        const div = document.createElement('div');
        div.className = 'autocomplete-suggestion';
        div.textContent = suggestion.station_name;
        div.addEventListener('click', function() {
            target.value = suggestion.station_code;
            clearSuggestions();
        });
        suggestionsContainer.appendChild(div);
    });
}

function clearSuggestions() {
    let suggestionsContainer = document.getElementById('suggestionsFrom');
    while (suggestionsContainer.firstChild) {
        suggestionsContainer.removeChild(suggestionsContainer.firstChild);
    }
    suggestionsContainer = document.getElementById('suggestionsTo');
    while (suggestionsContainer.firstChild) {
        suggestionsContainer.removeChild(suggestionsContainer.firstChild);
    }
}

document.getElementById('_from').addEventListener('input',(e)=>{suggestionHandler(e)});
document.getElementById('_to').addEventListener('input',(e)=>{suggestionHandler(e)});
