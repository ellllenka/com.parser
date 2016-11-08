'use strict';

// Register `phoneList` component, along with its associated controller and template
angular.module('matchList').component('matchList', {
    templateUrl: 'match-list/match-list.template.html',
    controller: ['$http', 'Match', function MatchListController($http, Match) {
        var date = new Date();
        date.setHours(0, 0, 0, 0);

        this.firstCategory = Match.query({category: 1});
        this.secondCategory = Match.query({category: 2});

        this.startParsing = function(){
            //$http({method: 'PUT', url: '/match', {number: "number"}}).then(function(){}, function(){});
            $http.put('/match', this.number).then(function(){}, function(){});
        };

        this.exportData = function () {
            var cat1 = new Blob([document.getElementById('category1').innerHTML], {
                type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"
            });
            saveAs(cat1, "Category1.xls");

            var cat2 = new Blob([document.getElementById('category2').innerHTML], {
                type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8"
            });

            saveAs(cat2, "Category2.xls");
        };

        this.clearDB = function () {
            $http({method: 'DELETE', url: '/match'});
        }
    }]
});

/*
 $http.put('/api/v1/users/' + user.login, { login: "login", password: "password" });
 */
