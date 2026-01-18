package fr.maxlego08.menu.common.utils.nms;

public enum EnumReflectionCompound {

        V1_8_8("getTag", "setTag", "hasKey", "getBoolean", "getFloat", "getDouble", "getLong", "getInt", "getString", "setBoolean", "setFloat", "setDouble", "setLong", "setInt", "setString"),

        V1_18_2("t", "c", "e", "q", "j", "k", "i", "h", "l", "a", "a", "a", "a", "a", "a"),

        V1_17("s", "c", "e", "q", "j", "k", "i", "h", "l", "a", "a", "a", "a", "a", "a"),

        V1_12("v", "c", "e", "q", "j", "k", "i", "h", "l", "a", "a", "a", "a", "a", "a"),

        V1_19("u", "c", "e", "q", "j", "k", "i", "h", "l", "a", "a", "a", "a", "a", "a"),

        ;

        private final String methodGetTag;
        private final String methodSetTag;

        private final String methodHasKey;

        private final String methodGetBoolean;
        private final String methodGetFloat;
        private final String methodGetDouble;
        private final String methodGetLong;
        private final String methodGetInt;
        private final String methodGetString;

        private final String methodSetBoolean;
        private final String methodSetFloat;
        private final String methodSetDouble;
        private final String methodSetLong;
        private final String methodSetInt;
        private final String methodSetString;

        EnumReflectionCompound(String methodGetTag, String methodSetTag, String methodHaskey, String methodGetBoolean, String methodGetFloat, String methodGetDouble, String methodGetLong, String methodGetInt, String methodGetString, String methodSetBoolean, String methodSetFloat, String methodSetDouble, String methodSetLong, String methodSetInt, String methodSetString) {
            this.methodGetTag = methodGetTag;
            this.methodSetTag = methodSetTag;
            this.methodHasKey = methodHaskey;
            this.methodGetBoolean = methodGetBoolean;
            this.methodGetFloat = methodGetFloat;
            this.methodGetDouble = methodGetDouble;
            this.methodGetLong = methodGetLong;
            this.methodGetInt = methodGetInt;
            this.methodGetString = methodGetString;
            this.methodSetBoolean = methodSetBoolean;
            this.methodSetFloat = methodSetFloat;
            this.methodSetDouble = methodSetDouble;
            this.methodSetLong = methodSetLong;
            this.methodSetInt = methodSetInt;
            this.methodSetString = methodSetString;
        }

        public String getMethodGetTag() {
            return methodGetTag;
        }

        public String getMethodSetTag() {
            return methodSetTag;
        }

        public String getMethodHasKey() {
            return methodHasKey;
        }

        public String getMethodGetBoolean() {
            return methodGetBoolean;
        }

        public String getMethodGetFloat() {
            return methodGetFloat;
        }

        public String getMethodGetDouble() {
            return methodGetDouble;
        }

        public String getMethodGetLong() {
            return methodGetLong;
        }

        public String getMethodGetInt() {
            return methodGetInt;
        }

        public String getMethodGetString() {
            return methodGetString;
        }

        public String getMethodSetBoolean() {
            return methodSetBoolean;
        }

        public String getMethodSetFloat() {
            return methodSetFloat;
        }

        public String getMethodSetDouble() {
            return methodSetDouble;
        }

        public String getMethodSetLong() {
            return methodSetLong;
        }

        public String getMethodSetInt() {
            return methodSetInt;
        }

        public String getMethodSetString() {
            return methodSetString;
        }

    }