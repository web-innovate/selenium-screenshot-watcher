var imgOneContainer = '#one',
    imgTwoContainer = '#two',
    imgThreeContainer = '#three';

$(imgOneContainer).scroll(function(el) {
    $(imgTwoContainer).scrollTop($(imgOneContainer).scrollTop());
    $(imgThreeContainer).scrollTop($(imgOneContainer).scrollTop());

    $(imgTwoContainer).scrollLeft($(imgOneContainer).scrollLeft());
    $(imgThreeContainer).scrollLeft($(imgOneContainer).scrollLeft());
});

$(imgTwoContainer).scroll(function(el) {
    $(imgOneContainer).scrollTop($(imgTwoContainer).scrollTop());
    $(imgThreeContainer).scrollTop($(imgTwoContainer).scrollTop());

    $(imgOneContainer).scrollLeft($(imgTwoContainer).scrollLeft());
    $(imgThreeContainer).scrollLeft($(imgTwoContainer).scrollLeft());
});

$(imgThreeContainer).scroll(function(el) {
    $(imgTwoContainer).scrollTop($(imgThreeContainer).scrollTop());
    $(imgOneContainer).scrollTop($(imgThreeContainer).scrollTop());

    $(imgTwoContainer).scrollLeft($(imgThreeContainer).scrollLeft());
    $(imgOneContainer).scrollLeft($(imgThreeContainer).scrollLeft());
});
