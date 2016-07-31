'use strict';

// Register `phoneList` component, along with its associated controller and template
angular.module('matchList').component('matchList', {
    templateUrl: 'match-list/match-list.template.html',
    controller: ['Match', function MatchListController(Match) {
        this.firstCategory = [
            {cmd1: 'Команда 1', cmd2: 'Команда 2', total: 10, ft: 12},
            {cmd1: 'Команда 1', cmd2: 'Команда 2', total: 10, ft: 12},
            {cmd1: 'Команда 1', cmd2: 'Команда 2', total: 10, ft: 12},
            {cmd1: 'Команда 1', cmd2: 'Команда 2', total: 10, ft: 12},
            {cmd1: 'Команда 1', cmd2: 'Команда 2', total: 10, ft: 12}
        ];

        this.secondCategory = [
            {cmd1: 'Команда 1', cmd2: 'Команда 2', total: 10, ft: 12},
            {cmd1: 'Команда 1', cmd2: 'Команда 2', total: 10, ft: 12},
            {cmd1: 'Команда 1', cmd2: 'Команда 2', total: 10, ft: 12},
            {cmd1: 'Команда 1', cmd2: 'Команда 2', total: 10, ft: 12},
            {cmd1: 'Команда 1', cmd2: 'Команда 2', total: 10, ft: 12}
        ]
    }]
});
 
