/* Util Functions */

function escapeRegExp(string) {
    return string.replace(/([.*+?^=!:${}()|\[\]\/\\])/g, "\\$1");
}

function replaceAll(find, replace, str) {
  return str.replace(new RegExp(escapeRegExp(find), 'g'), replace);
}

/* Page.js */

var members = ['yurifariasg', 'tferreirap', 'bruno.paiva.5283', 'paulo.rolgan'];

var changeImageCallback = function(imageId) {
    return function(data, textStatus) {
      $('#' + replaceAll(".", "", imageId)).attr("src", data.data.url);
    };
};

for (var i = 0 ; i < members.length ; i++) {
  $.get(
      'https://graph.facebook.com/' + members[i] + '/picture',
      {'redirect': 0, 'type': 'normal', 'width': 130, 'height': 130},
      changeImageCallback(members[i])
    );
}