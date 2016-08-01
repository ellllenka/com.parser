'use strict';

// Register `phoneList` component, along with its associated controller and template
angular.module('matchList').component('matchList', {
    templateUrl: 'match-list/match-list.template.html',
    controller: ['Match', function MatchListController(Match) {
        var date = new Date();
        date.setHours(0,0,0,0);
        this.firstCategory = Match.query({category: 1});

        this.secondCategory = Match.query({category: 2});


    }]
});
 
