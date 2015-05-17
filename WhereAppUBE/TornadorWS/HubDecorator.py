from inspect import getargspec
import inspect
import json
import string
import sys
import logging
import logging.config

logging.config.dictConfig(json.load(open('logging.json')))

from HubDecoratorConfig import HubDecoratorConfig as config
import logging.config

log = logging.getLogger(__name__)
HUBs_DICT = {}
__HUBsJS_Strings = []
__HUBsJAVA_Strings = []

def __getPublicFunctions(m):
    isFunction = inspect.ismethod if sys.version_info[0] == 2 else inspect.isfunction
    return isFunction(m) and not m.__name__.startswith("_")

__ASCII_UpperCase = string.uppercase if sys.version_info[0] == 2 else string.ascii_uppercase


def __constructJSFile(path = ""):
    with open(path + "tornadoProtocol.js", "w") as f:
        f.write(config.JS_WRAPPER.format(main="".join(__HUBsJS_Strings)))


def __constructJAVAFile(path ,package):
    with open(path +"/TornadoServer.java", "w") as f:
        wrapper = config.JAVA_WRAPPER%package
        f.write(wrapper.format(main="".join(__HUBsJAVA_Strings)))


#todo: make a class to story global functions and variables
def HubDecorator(cls):  # todo: check if __init__ has no parameters
    JS, JAVA = range(2)

    def getArgs(m):
        args = getargspec(m).args
        for arg in ("self", "_client"):
            try:
                args.remove(arg)
            except:
                pass

        return args

    def getDefaultsTemplates(m, template):
        if template is None: return []
        n = getArgs(m)
        d = getargspec(m).defaults
        if d is None: return []
        return [template.format(iter=i, name=n[len(n) - len(d) + i], default=d[i]) for i in range(len(d))]

    def getJSFunctionsStr(cls, templates):
        funcStrings = []
        functions = inspect.getmembers(cls, predicate=__getPublicFunctions)
        for name, m in functions:
            defaults = "\n\t\t\t".join(getDefaultsTemplates(m, templates.argsCook))
            funcStrings.append(templates.function.format(name=name, args=", ".join(getArgs(m)), cook=defaults))
        return funcStrings

    def getToJsonTemplates(m, template):
        if template is None: return []
        args = getArgs(m)
        return [template.format(arg = a) for a in args]

    def getJAVAFunctionsStr(cls, templates):
        funcStrings = []
        functions = inspect.getmembers(cls, predicate=__getPublicFunctions)
        for name, m in functions:
            args = getArgs(m)
            types = ["TYPE_" + l for l in __ASCII_UpperCase[:len(args)]]
            args = [types[i] + " " + arg for i, arg in enumerate(args)]
            types = "<" + ", ".join(types) + ">" if len(types) > 0 else ""
            toJson = "\n\t\t\t".join(getToJsonTemplates(m,templates.argsCook))
            funcStrings.append(templates.function.format(name=name, args=", ".join(args), types=types, cook = toJson))
        return funcStrings

    def getHubClass(templates, mode=JS):
        if mode == JS:
            funcStrings = ",\n".join(getJSFunctionsStr(cls, templates))
        elif mode == JAVA:
            funcStrings = "\n".join(getJAVAFunctionsStr(cls, templates))

        return templates.class_.format(name=cls.__name__, functions=funcStrings)

    __HUBsJS_Strings.append(getHubClass(config.getJSTemplates(),JS))
    __HUBsJAVA_Strings.append(getHubClass(config.getJAVATemplates(),JAVA))

    HUBs_DICT[cls.__name__] = cls()
    return cls

if __name__ == '__main__':
    @HubDecorator
    class TestClass:
        def test(self, a=1, b=2):
            print(a, b)

        def tast(self, a=5, b=1, c=3):
            print(a, b)

    @HubDecorator
    class TestClass2:
        def test(self, a=1, b=2):
            print(a, b)

        def tast(self, a=5, b=1, c=3):
            print(a, b)

    __constructJAVAFile("C:/Users/Jorge/SoftwareProjects/WhereAppU/app/src/main/java/com/application/jorge/whereappu/Tornado","com.application.jorge.whereappu.Tornado")
    __constructJSFile()
