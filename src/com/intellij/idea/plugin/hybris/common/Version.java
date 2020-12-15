/*
 * This file is part of "hybris integration" plugin for Intellij IDEA.
 * Copyright (C) 2014-2016 Alexander Bartash <AlexanderBartash@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.intellij.idea.plugin.hybris.common;

import java.util.Comparator;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Version implements Comparable<Version> {

    private static final String SNAPSHOT = "-SNAPSHOT";
    private static final Pattern NEW_VERSION = Pattern.compile("(\\d\\d)(\\d\\d)(\\.([1-9]?\\d))?");
    private static final Pattern OLD_VERSION =
        Pattern.compile("(\\d)\\.(\\d)\\.(\\d)(\\.([1-9]?\\d))?");
    public static final Version UNDEFINED = new Version(Integer.MAX_VALUE, Integer.MAX_VALUE,
                                                        Integer.MAX_VALUE, Integer.MAX_VALUE, "<undefined>");
    public static final Comparator<Version> VERSION_COMPARATOR =
        Comparator.comparingInt(Version::getMajor).thenComparingInt(Version::getMinor)
                  .thenComparingInt(Version::getRelease).thenComparingInt(Version::getPatch);

    private final int major;
    private final int minor;
    private final int release;
    private final int patch;
    private final String original;

    private Version(int major, int minor, int release, int patch, String original) {
        this.major = major;
        this.minor = minor;
        this.release = release;
        this.patch = patch;
        this.original = original;
    }

    public static Version parseVersion(String v) {
        Objects.requireNonNull(v, "Version must not be null");
        if (v.isEmpty()) {
            throw new IllegalArgumentException("version must not be empty");
        }

        final String fixedVersion = v.replace(SNAPSHOT, "");

        Matcher oldV = OLD_VERSION.matcher(fixedVersion);
        Matcher newV = NEW_VERSION.matcher(fixedVersion);

        if (newV.matches()) {
            int patch = 0;
            if (newV.groupCount() > 3 && newV.group(4) != null) {
                patch = Integer.parseInt(newV.group(4));
            }
            return new Version(Integer.parseInt(newV.group(1)), Integer.parseInt(newV.group(2)), 0,
                               patch, fixedVersion);
        } else if (oldV.matches()) {
            int patch = 0;
            if (oldV.groupCount() > 4 && oldV.group(5) != null) {
                patch = Integer.parseInt(oldV.group(5));
            }
            return new Version(Integer.parseInt(oldV.group(1)), Integer.parseInt(oldV.group(2)),
                               Integer.parseInt(oldV.group(3)), patch, fixedVersion);
        }
        String[] split = fixedVersion.split("\\.");
        int major = 0, minor = 0, release = 0, patch = 0;
        switch (split.length) {
            case 4:
                patch = Integer.parseInt(split[3]);
            case 3:
                release = Integer.parseInt(split[2]);
            case 2:
                minor = Integer.parseInt(split[1]);
            case 1:
                major = Integer.parseInt(split[0]);
                break;
            default:
                throw new IllegalArgumentException("Could not parse " + fixedVersion);
        }
        return new Version(major, minor, release, patch, fixedVersion);
    }

    @Override
    public int compareTo(Version o) {
        return VERSION_COMPARATOR.compare(this, o);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Version version = (Version) o;
        return major == version.major && minor == version.minor && release == version.release
               && patch == version.patch;
    }

    public boolean equalsIgnoringPatchLevel(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Version version = (Version) o;
        return major == version.major && minor == version.minor && release == version.release;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, release, patch);
    }

    @Override
    public String toString() {
        return original;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getRelease() {
        return release;
    }

    public int getPatch() {
        return patch;
    }
}

