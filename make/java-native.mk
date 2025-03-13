#
# Copyright (C) 2023-2024 Objectos Software LTDA.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

#
# Creates a GraalVM native image
#

## configures NATIVE_IMAGE
ifdef GRAALVM_HOME
NATIVE_IMAGE := $(GRAALVM_HOME)/bin/native-image
else
$(error Required GRAALVM_HOME variable was not set)
endif

## app module name
ifndef MODULE
ifdef ARTIFACT_ID
MODULE := $(ARTIFACT_ID)
else
$(error The required variable MODULE was not defined)
endif
endif

## used by the filename
ifndef ARTIFACT_ID
$(error The required variable ARTIFACT_ID was not defined)
endif

## app version
ifndef VERSION
$(error The required variable VERSION was not defined)
endif

## native main class
ifndef NATIVE_MAIN
$(error The required variable NATIVE_MAIN was not defined)
endif

## native output dir
NATIVE_OUTPUT := $(WORK)/bin

## native file name
NATIVE_FILENAME := $(ARTIFACT_ID)-$(VERSION)

## native output file
NATIVE_OUTPUT_FILE := $(NATIVE_OUTPUT)/$(NATIVE_FILENAME)

## native dependencies
ifdef NATIVE_DEPS
NATIVE_RESOLUTION_FILES := $(call to-resolution-files,$(NATIVE_DEPS))
endif

## native module-path
NATIVE_MODULE_PATH := $(WORK)/native-module-path

## native build args
# NATIVE_BUILD_ARGS

## native-image command
NATIVE_IMAGEX := $(NATIVE_IMAGE)
ifdef NATIVE_BUILD_ARGS
NATIVE_IMAGEX += $(NATIVE_BUILD_ARGS)
endif
NATIVE_IMAGEX += -o $(NATIVE_OUTPUT_FILE)
NATIVE_IMAGEX += --module-path @$(NATIVE_MODULE_PATH)
NATIVE_IMAGEX += --module $(MODULE)/$(NATIVE_MAIN)


.PHONY: native
native: $(NATIVE_MODULE_PATH) $(NATIVE_OUTPUT_FILE)

.PHONY: native@clean
native@clean:
	rm -f $(NATIVE_MODULE_PATH) $(NATIVE_OUTPUT_FILE)

.PHONY: re-native
re-native: native@clean native
	
$(NATIVE_MODULE_PATH): $(COMPILE_MARKER) $(NATIVE_RESOLUTION_FILES)
	echo $(CLASS_OUTPUT) > $@.tmp
ifdef COMPILE_RESOLUTION_FILES
	cat $(COMPILE_RESOLUTION_FILES) >> $@.tmp
endif
ifdef NATIVE_RESOLUTION_FILES
	cat $(NATIVE_RESOLUTION_FILES) >> $@.tmp
endif
	cat $@.tmp | paste --delimiter='$(MODULE_PATH_SEPARATOR)' --serial > $@

$(NATIVE_OUTPUT):
	mkdir --parents $@

$(NATIVE_OUTPUT_FILE): | $(NATIVE_OUTPUT)
	$(NATIVE_IMAGEX)
