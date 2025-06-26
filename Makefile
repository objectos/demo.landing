#
# Copyright (C) 2024-2025 Objectos Software LTDA.
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
# Demo Landing
#

## Coordinates
GROUP_ID := br.com.objectos
ARTIFACT_ID := demo.landing
VERSION := 002-SNAPSHOT
MODULE := $(ARTIFACT_ID)

## javac --release option
JAVA_RELEASE := 24

## Maven interop
REMOTE_REPOS := https://repo.maven.apache.org/maven2

## Dependencies
H2_SRC := com.h2database/h2/2.3.232
H2_LOCAL := br.com.objectos/h2/2.2.232
WAY := br.com.objectos/objectos.way/0.2.5-SNAPSHOT

SLF4J_API := org.slf4j/slf4j-api/1.7.36
SLF4J_NOP := org.slf4j/slf4j-nop/1.7.36
TESTNG := org.testng/testng/7.10.2

# Delete the default suffixes
.SUFFIXES:

#
# landing
#

.PHONY: all
all: test

include make/java-core.mk

#
# landing@clean
#

include make/common-clean.mk

#
# landing@h2
#
# creates alternate h2.jar containing module-info.class
#

include make/java-module-info.mk

com.h2database_MULTI_RELEASE := $(JAVA_RELEASE)
com.h2database_IGNORE_MISSING_DEPS := 1
$(eval $(call module-info,com.h2database,$(H2_LOCAL),$(H2_SRC),$(SLF4J_API)))

#
# landing@compile
#

## compile deps
COMPILE_DEPS := $(WAY)
COMPILE_DEPS += $(H2_LOCAL)

## compilation requirements 
COMPILE_REQS = $(SOURCE_GEN)

## resources directory
RESOURCES := resources

include make/java-compile.mk

#
# landing@source-gen
#

## source-gen
SOURCE_GEN := $(MAIN)/demo/landing/app/Source.java

## source-gen java command
SOURCE_GEN_JAVAX := $(JAVA)
SOURCE_GEN_JAVAX += SourceGen.java

## when to update
SOURCE_GEN_REQS := $(filter-out $(SOURCE_GEN),$(wildcard $(MAIN)/demo/landing/app/*.java))

.PHONY: source-gen
source-gen: $(SOURCE_GEN)

.PHONY: source-gen@clean
source-gen@clean:
	rm -f $(SOURCE_GEN)

.PHONY: source-gen@test
source-gen@test:
	$(SOURCE_GEN_JAVAX) test

$(SOURCE_GEN): $(SOURCE_GEN_REQS)
	$(SOURCE_GEN_JAVAX)

#
# landing@db
#

## db path
DB := $(WORK)/$(MODULE)

## db file
DB_FILE := $(DB).mv.db

## db resolution files
DB_RESOLUTION_FILES := $(call to-resolution-files,$(COMPILE_DEPS))

## db module-path
DB_MODULE_PATH := $(WORK)/db-module-path

## db-run java command
DB_RUN_JAVAX := $(JAVA)
DB_RUN_JAVAX += --module-path @$(DB_MODULE_PATH)
DB_RUN_JAVAX += --module com.h2database/org.h2.tools.Shell
DB_RUN_JAVAX += -url "jdbc:h2:file:$(abspath $(DB));IFEXISTS=TRUE"
DB_RUN_JAVAX += -user sa
DB_RUN_JAVAX += -password ""
#DB_RUN_JAVAX += --module com.h2database/org.h2.tools.Server
#DB_RUN_JAVAX += -ifExists -tcp -web

.PHONY: db-clean
db-clean:
	rm -f $(DB_FILE)

.PHONY: db-run
db-run: $(DB_MODULE_PATH)
	$(DB_RUN_JAVAX)

$(DB_MODULE_PATH): $(COMPILE_MARKER) $(DB_RESOLUTION_FILES)
	echo $(CLASS_OUTPUT) > $@.tmp
	cat $(DB_RESOLUTION_FILES) >> $@.tmp
	cat $@.tmp | paste --delimiter='$(MODULE_PATH_SEPARATOR)' --serial > $@

#
# landing@test-compile
#

## test compile deps
TEST_COMPILE_DEPS := $(TESTNG)

include make/java-test-compile.mk

#
# landing@test
#

## test main class
TEST_MAIN := demo.landing.StartTest

## www test runtime dependencies
TEST_RUNTIME_DEPS := $(SLF4J_NOP)

## test --add-modules
TEST_ADD_MODULES := org.testng

## test --add-exports
TEST_ADD_EXPORTS := demo.landing/demo.landing.app=org.testng
TEST_ADD_EXPORTS += demo.landing/demo.landing.local=org.testng

## test --add-reads
TEST_ADD_READS := demo.landing=org.testng

## test args
TEST_ARGS := -d $(WORK)/test-output
TEST_ARGS += $(TEST)/testng.xml

include make/java-test.mk

#
# landing@dev
#

## dev main class
DEV_MAIN := demo.landing.StartDev

## dev jvm opts
DEV_JVM_OPTS := -Xmx64m
DEV_JVM_OPTS += -XX:+UseSerialGC
ifeq ($(ENABLE_DEBUG),1)
DEV_JVM_OPTS += -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=localhost:7000
endif

## dev app args
DEV_APP_ARGS := --class-output $(CLASS_OUTPUT)

include make/java-dev.mk

#
# landing@jar
#

include make/java-jar.mk

#
# landing@install
#

include make/java-install.mk


#
# landing@eclipse
#

include make/java-eclipse.mk

#
# GH secrets
#

## - GH_TOKEN
-include $(HOME)/.config/objectos/gh-config.mk

#
# landing@gh-release
#

include make/gh-release.mk
