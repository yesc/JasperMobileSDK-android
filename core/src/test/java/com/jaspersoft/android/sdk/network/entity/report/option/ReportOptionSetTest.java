/*
 * Copyright (C) 2016 TIBCO Jaspersoft Corporation. All rights reserved.
 * http://community.jaspersoft.com/project/mobile-sdk-android
 *
 * Unless you have purchased a commercial license agreement from TIBCO Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of TIBCO Jaspersoft Mobile SDK for Android.
 *
 * TIBCO Jaspersoft Mobile SDK is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TIBCO Jaspersoft Mobile SDK is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TIBCO Jaspersoft Mobile SDK for Android. If not, see
 * <http://www.gnu.org/licenses/lgpl>.
 */

package com.jaspersoft.android.sdk.network.entity.report.option;

import com.google.gson.annotations.Expose;

import org.junit.Test;

import java.lang.reflect.Field;

import static com.jaspersoft.android.sdk.test.matcher.HasAnnotation.hasAnnotation;
import static com.jaspersoft.android.sdk.test.matcher.HasSerializedName.hasSerializedName;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Tom Koptel
 * @since 2.3
 */
public class ReportOptionSetTest {
    @Test
    public void shouldHaveExposeAnnotationForFieldOptions() throws NoSuchFieldException {
        Field field = ReportOptionSet.class.getDeclaredField("mOptions");
        assertThat(field, hasAnnotation(Expose.class));
    }

    @Test
    public void optionsFieldShouldHaveSerializedNameAnnotationForField() throws NoSuchFieldException {
        Field field = ReportOptionSet.class.getDeclaredField("mOptions");
        assertThat(field, hasSerializedName("reportOptionsSummary"));
    }
}