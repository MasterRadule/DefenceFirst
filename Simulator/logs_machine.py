from abc import ABC, abstractmethod
from random import randint
from time import sleep


class State(ABC):
    @abstractmethod
    def run(self, context):
        return NotImplemented


class DoSAttack(State):
    def run(self, context):
        print("DOS")
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
