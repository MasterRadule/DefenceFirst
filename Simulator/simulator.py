import logging
import os
import random
from abc import ABC, abstractmethod
from random import randint
from time import sleep, strftime

HOSTNAME = ['defence-first.rs', 'defence-first.de', 'defence-first.ru']
HOSTIP = ['78.218.236.218', '87.236.11.212', '54.147.165.86']
SOURCEIP = ['163.189.141.53', '204.164.10.7', '213.166.160.236', '123.197.235.233', '77.28.21.14']
USERNAMES = ['user1', 'user2', 'user3', 'user4', 'user5']
FACILITY = ['KERN', 'USER', 'MAIL', 'DAEMON', 'AUTH', 'SYSLOG', 'LPR', 'NEWS',
            'UUCP', 'CLOCK_DAEMON', 'AUTHPRIV', 'FTP', 'NTP', 'LOGAUDIT', 'LOGALERT',
            'CRON', 'LOCAL0', 'LOCAL1', 'LOCAL2', 'LOCAL3', 'LOCAL4', 'LOCAL5', 'LOCAL6', 'LOCAL7']
SEVERITY = ['DEBUG', 'INFORMATIONAL', 'NOTICE', 'WARNING', 'ERROR', 'CRITICAL', 'ALERT', 'EMERGENCY']
FORMAT = '%(asctime)s     %(hostname)s-Application-%(hostip)s-%(sourceip)s     %(severity)s-%(facility)s     %(' \
         'message)s '
RESOURCES = ['index.html', 'document.xml', 'dashboard.html']

LOGS_PATH = 'logs'


class State(ABC):
    @abstractmethod
    def run(self, context):
        return NotImplemented


class DoSAttack(State):
    def run(self, context):
        d = {'hostname': HOSTNAME[0], 'hostip': HOSTIP[0], 'severity': SEVERITY[1],
             'facility': FACILITY[1]}

        http_response_code = '200'
        for i in range(25):
            if i >= 20:
                http_response_code = '503'
                d['severity'] = SEVERITY[5]

            for sourceip in SOURCEIP:
                d['sourceip'] = sourceip
                context.logger.info('Requested resource index.html {}'.format(http_response_code), extra=d)

        context.state = NormalState()


class NormalState(State):
    def run(self, context):
        normal = {'hostname': HOSTNAME[1], 'hostip': HOSTIP[1], 'severity': SEVERITY[1],
                  'facility': FACILITY[1]}

        while True:
            normal['sourceip'] = random.choice(SOURCEIP)
            if random.random() < 0.3:
                context.logger.info(
                    'Successful authorization on username "{}"'.format(USERNAMES[SOURCEIP.index(normal['sourceip'])]),
                    extra=normal)
            else:
                context.logger.info('Requested resource {} 200'.format(random.choice(RESOURCES)), extra=normal)
            sleep(1)

            if random.random() < 0.1:
                rand = randint(1, 3)

                if rand == 1:
                    context.state = DoSAttack()
                elif rand == 2:
                    context.state = BruteForce()
                elif rand == 3:
                    context.state = DatabaseError()

                context.state.run(context)


class BruteForce(State):
    def run(self, context):
        attack = {'hostname': HOSTNAME[1], 'hostip': HOSTIP[1], 'sourceip': SOURCEIP[0], 'severity': SEVERITY[2],
                  'facility': FACILITY[4]}
        normal = {'hostname': HOSTNAME[1], 'hostip': HOSTIP[1], 'severity': SEVERITY[1],
                  'facility': FACILITY[1]}

        for i in range(30):
            if i > 5:
                attack['severity'] = SEVERITY[3]

            if random.random() < 0.45:
                normal['sourceip'] = random.choice(SOURCEIP)
                context.logger.info('Requested resource {} 200'.format(random.choice(RESOURCES)), extra=normal)
                sleep(0.5)

            context.logger.info('Failed authorization on username "user1"', extra=attack)
            sleep(0.5)

        context.state = NormalState()


class DatabaseError(State):
    def run(self, context):
        d = {'hostname': HOSTNAME[2], 'hostip': HOSTIP[2], 'sourceip': SOURCEIP[0], 'severity': SEVERITY[4],
             'facility': FACILITY[3]}

        context.logger.info('Database error', extra=d)
        sleep(1)

        context.state = NormalState()


class Context:
    def __init__(self):
        self.state = NormalState()
        formatter = logging.Formatter(FORMAT, "%Y-%m-%d %H:%M:%S")

        logger = logging.getLogger('simulator')

        if not os.path.exists(LOGS_PATH):
            os.mkdir(LOGS_PATH)

        fileHandler = logging.FileHandler(
            os.path.join(LOGS_PATH, 'application_log-{}.log'.format(strftime('%Y-%M-%d'))))
        fileHandler.setFormatter(formatter)

        consoleHandler = logging.StreamHandler()
        consoleHandler.setFormatter(formatter)

        logger.addHandler(fileHandler)
        logger.addHandler(consoleHandler)

        logger.setLevel(logging.INFO)

        self.logger = logger

    def run(self):
        self.state.run(self)

    @property
    def state(self):
        return self._state

    @state.setter
    def state(self, value):
        self._state = value


if __name__ == '__main__':
    sm = Context()
    sm.run()
