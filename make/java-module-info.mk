#
# Copyright (C) 2024 Objectos Software LTDA.
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
# module-info task
#
# $(1) = module name
# $(2) = dst gav
# $(3) = src gav
# $(4) = deps

ifndef WORK
$(error Required common-clean.mk was not included)
endif

ifndef gav-to-resolution-file
$(error Required java-core.mk was not included)
endif

define module-info

## final resolution file
$(1)RESOLUTION_FILE := $$(call gav-to-resolution-file,$(2))

## final JAR file name
$(1)JAR_FILE := $$(call gav-to-local,$(2))

## work modified JAR file name
$(1)JAR_MOD := $$(WORK)/$(1).jar

## work source JAR file name
$(1)JAR_SRC := $$(WORK)/relocated/$(1).jar

## module-info.java
$(1)MODULE_INFO := modules/$(1)/module-info.java

## src resolution file
$(1)SRC_RESOLUTION_FILE := $$(call gav-to-resolution-file,$(3))

## module path (if necessary)
ifneq ($(4),)

## deps resolution files
$(1)DEPS_RESOLUTIONS := $$(call to-resolution-files,$(4))

## deps module path
$(1)MODULE_PATH := $$(WORK)/$(1)-module-path

endif

## src jar file
$(1)SRC_JAR_FILE := $$(call gav-to-local,$(3))

## the jdeps command
$(1)JDEPSX  = $$(JDEPS)
ifdef $(1)MODULE_PATH
$(1)JDEPSX += --module-path $$(file < $$($(1)MODULE_PATH))
endif
ifeq ($$($(1)_IGNORE_MISSING_DEPS),1)
$(1)JDEPSX += --ignore-missing-deps
endif
ifdef $(1)_MULTI_RELEASE
$(1)JDEPSX += --multi-release $$($(1)_MULTI_RELEASE)
endif
$(1)JDEPSX += --generate-module-info modules
$(1)JDEPSX += $$($(1)JAR_SRC)

## javac output directory
$(1)CLASS_OUTPUT := $$(WORK)/$(1)

## module-info.class
$(1)CLASSES := $$($(1)CLASS_OUTPUT)/module-info.class

ifndef $(1)_JAVA_RELEASE
ifdef JAVA_RELEASE
$(1)_JAVA_RELEASE := $$(JAVA_RELEASE)
endif
endif

## the javac command
$(1)JAVACX  = $$(JAVAC)
$(1)JAVACX += -d $$($(1)CLASS_OUTPUT)
ifdef $(1)MODULE_PATH
$(1)JAVACX += --module-path @$$($(1)MODULE_PATH)
endif
ifdef $(1)_JAVA_RELEASE
$(1)JAVACX += --release $$($(1)_JAVA_RELEASE)
endif
$(1)JAVACX += --patch-module $(1)=$$($(1)JAR_SRC)
$(1)JAVACX += $$($(1)MODULE_INFO)
ifdef $(1)_EXTRAS
$(1)JAVACX += $$($(1)_EXTRAS)
endif

#
# foo
#

.PHONY: $(1)
$(1): $$($(1)RESOLUTION_FILE)

#
# foo@clean
#

.PHONY: $(1)@clean
$(1)@clean:
	rm -f $$($(1)JAR_FILE) $$($(1)RESOLUTION_FILE) $$($(1)TMP) $$($(1)MODULE_PATH)
	
.PHONY: re-$(1)
re-$(1): $(1)@clean $(1)

$$($(1)MODULE_PATH): $$($(1)DEPS_RESOLUTIONS) | $$(WORK)
	cat $$^ | sort -u > $$@.tmp
	cat $$@.tmp | paste --delimiter='$(MODULE_PATH_SEPARATOR)' --serial > $$@

$$($(1)JAR_SRC): $$($(1)SRC_RESOLUTION_FILE)
	@mkdir --parents $$(@D)
	cp $$($(1)SRC_JAR_FILE) $$@

$$($(1)MODULE_INFO):
	$$($(1)JDEPSX)

$$($(1)CLASSES): $$($(1)MODULE_PATH) $$($(1)JAR_SRC) $$($(1)MODULE_INFO) 
	rm -rf $$($(1)CLASS_OUTPUT)
	$$($(1)JAVACX)
	
$$($(1)JAR_MOD): $$($(1)CLASSES)
	cp $$($(1)JAR_SRC) $$@
	$(JAR) --update --file=$$@ -C $$($(1)CLASS_OUTPUT) .

$$($(1)JAR_FILE): $$($(1)JAR_MOD)
	@mkdir --parents $$(@D)
	cp $$< $$@

$$($(1)RESOLUTION_FILE): $$($(1)JAR_FILE)
	@mkdir --parents $$(@D)
	echo "$$<" > $$@
ifdef $(1)DEPS_RESOLUTIONS
	cat $$($(1)DEPS_RESOLUTIONS) >> $$@
endif
	
endef
