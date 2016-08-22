var imageOne = '#one>img',
    imageTwo = '#two>img',
    imageThree = '#three>img';

$('.asExpected').click(function() {
    showConfirmationDialog();
});

function getImageId($link) {
    var items = $link.attr('src').split('/');
    var idIndex = items.length;
    return items[idIndex - 1];
}
