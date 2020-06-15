from cx_Freeze import setup, Executable

base = None

executables = [Executable("logs_machine.py", base=base)]

packages = ["logging", "os", "random", "abc", "time"]
options = {
    'build_exe': {
        'packages': packages,
    },
}

setup(
    name="Simulator",
    options=options,
    version="1.0",
    description='Create logs using state machine',
    executables=executables
)
