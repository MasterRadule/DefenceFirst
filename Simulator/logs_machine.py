import logging
from abc import ABC, abstractmethod
from random import randint
from time import sleep

OS = ['Windows', 'Linux']
HOSTNAME = ['defence-first.rs', 'defence-first.de', 'defence-first.ru']
HOSTIP = ['78.218.236.218', '87.236.11.212', '54.147.165.86']
SOURCEIP = ['163.189.141.53', '204.164.10.7', '213.166.160.236', '123.197.235.233', '77.28.21.14']
FACILITY = ['KERN', 'USER', 'MAIL', 'DAEMON', 'AUTH', 'SYSLOG', 'LPR', 'NEWS',
            'UUCP', 'CLOCK_DAEMON', 'AUTHPRIV', 'FTP', 'NTP', 'LOGAUDIT', 'LOGALERT',
            'CRON', 'LOCAL0', 'LOCAL1', 'LOCAL2', 'LOCAL3', 'LOCAL4', 'LOCAL5', 'LOCAL6', 'LOCAL7']
FORMAT = '%(asctime)s     %(hostname)s-%(os)s-%(hostip)s-%(sourceip)s     %(levelname)s-%(facility)s     %(message)s'


class State(ABC):
    @abstractmethod
    def run(self, context):
        return NotImplemented


class DoSAttack(State):
    def run(self, context):
        d1 = {'hostname': HOSTNAME[0], 'os': OS[1], 'hostip': HOSTIP[0], 'sourceip': SOURCEIP[0],
              'facility': FACILITY[1]}
        d2 = {'hostname': HOSTNAME[0], 'os': OS[1], 'hostip': HOSTIP[0], 'sourceip': SOURCEIP[1],
              'facility': FACILITY[1]}
        d3 = {'hostname': HOSTNAME[0], 'os': OS[1], 'hostip': HOSTIP[0], 'sourceip': SOURCEIP[2],
              'facility': FACILITY[1]}
        d4 = {'hostname': HOSTNAME[0], 'os': OS[1], 'hostip': HOSTIP[0], 'sourceip': SOURCEIP[3],
              'facility': FACILITY[1]}
        d5 = {'hostname': HOSTNAME[0], 'os': OS[1], 'hostip': HOSTIP[0], 'sourceip': SOURCEIP[4],
              'facility': FACILITY[1]}

        for i in range(20):
            context.logger.info('Requested resource index.html', extra=d1)
            sleep(0.1)
            context.logger.info('Requested resource index.html', extra=d2)
            sleep(0.1)
            context.logger.info('Requested resource index.html', extra=d3)
            sleep(0.1)
            context.logger.info('Requested resource index.html', extra=d4)
            sleep(0.1)
            context.logger.info('Requested resource index.html', extra=d5)

        context.state = NormalState()


class NormalState(State):
    def run(self, context):
        print("Normal")
        rand = randint(1, 3)

        if rand == 1:
            context.state = DoSAttack()
        elif rand == 2:
            context.state = BruteForce()
        elif rand == 3:
            context.state = AccountMisusage()


class BruteForce(State):
    def run(self, context):
        print("Brute force")
        context.state = NormalState()


class AccountMisusage(State):
    def run(self, context):
        print("Acount misusage")
        context.state = NormalState()


class Context:
    def __init__(self):
        self.state = NormalState()
        logging.basicConfig(format=FORMAT, datefmt='%Y-%m-%dT%H:%M:%S%z')
        logger = logging.getLogger('simulator')
        logger.setLevel(logging.INFO)

        self.logger = logger

    def run(self):
        self.state.run(self)
        sleep(3)

    @property
    def state(self):
        return self._state

    @state.setter
    def state(self, value):
        self._state = value


if __name__ == '__main__':
    sm = Context()

    while True:
        sm.run()
