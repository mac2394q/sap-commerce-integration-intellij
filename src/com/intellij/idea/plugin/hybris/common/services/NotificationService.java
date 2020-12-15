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

package com.intellij.idea.plugin.hybris.common.services;

import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import org.jetbrains.annotations.NotNull;

/**
 * Provides an abstraction for Intellij API for showing notifications and simplifies the API.
 *
 * @author Vlad Bozhenok <vladbozhenok@gmail.com>
 */
public interface NotificationService {

    void showNotification(
        @NotNull String contentI18nKey,
        @NotNull NotificationType messageLevel
    );

    void showNotificationWithCloseTimeout(
        @NotNull String contentI18nKey,
        @NotNull NotificationType messageLevel,
        int timeoutMilliseconds
    );

    void showImportantNotification(
        @NotNull String contentI18nKey,
        @NotNull NotificationType messageLevel
    );

    void showImportantNotificationWithCloseTimeout(
        @NotNull String contentI18nKey,
        @NotNull NotificationType messageLevel,
        int timeoutMilliseconds
    );

    void showNotification(
        @NotNull String contentI18nKey,
        @NotNull NotificationType messageLevel,
        @NotNull NotificationListener listener
    );

    void showNotificationWithCloseTimeout(
        @NotNull String contentI18nKey,
        @NotNull NotificationType messageLevel,
        int timeoutMilliseconds,
        @NotNull NotificationListener listener
    );

    void showImportantNotification(
        @NotNull String contentI18nKey,
        @NotNull NotificationType messageLevel,
        @NotNull NotificationListener listener
    );

    void showImportantNotificationWithCloseTimeout(
        @NotNull String contentI18nKey,
        @NotNull NotificationType messageLevel,
        int timeoutMilliseconds,
        @NotNull NotificationListener listener
    );

    void showNotification(
        @NotNull String titleI18nKey,
        @NotNull String contentI18nKey,
        @NotNull NotificationType messageLevel
    );

    void showNotificationWithCloseTimeout(
        @NotNull String titleI18nKey,
        @NotNull String contentI18nKey,
        int timeoutMilliseconds,
        @NotNull NotificationType messageLevel
    );

    void showImportantNotification(
        @NotNull String titleI18nKey,
        @NotNull String contentI18nKey,
        @NotNull NotificationType messageLevel
    );

    void showImportantNotificationWithCloseTimeout(
        @NotNull String titleI18nKey,
        @NotNull String contentI18nKey,
        @NotNull NotificationType messageLevel,
        int timeoutMilliseconds
    );

    void showNotification(
        @NotNull String titleI18nKey,
        @NotNull String contentI18nKey,
        @NotNull NotificationType messageLevel,
        @NotNull NotificationListener listener
    );

    void showNotificationWithCloseTimeout(
        @NotNull String titleI18nKey,
        @NotNull String contentI18nKey,
        @NotNull NotificationType messageLevel,
        int timeoutMilliseconds,
        @NotNull NotificationListener listener
    );

    void showImportantNotification(
        @NotNull String titleI18nKey,
        @NotNull String contentI18nKey,
        @NotNull NotificationType messageLevel,
        @NotNull NotificationListener listener
    );

    void showImportantNotificationWithCloseTimeout(
        @NotNull String titleI18nKey,
        @NotNull String contentI18nKey,
        @NotNull NotificationType messageLevel,
        int timeoutMilliseconds,
        @NotNull NotificationListener listener
    );

    void showNotification(
        @NotNull String titleI18nKey,
        @NotNull String subtitleI18nKey,
        @NotNull String contentI18nKey,
        @NotNull NotificationType messageLevel
    );

    void showNotificationWithCloseTimeout(
        @NotNull String titleI18nKey,
        @NotNull String subtitleI18nKey,
        @NotNull String contentI18nKey,
        @NotNull NotificationType messageLevel,
        int timeoutMilliseconds
    );

    void showImportantNotification(
        @NotNull String titleI18nKey,
        @NotNull String subtitleI18nKey,
        @NotNull String contentI18nKey,
        @NotNull NotificationType messageLevel
    );

    void showImportantNotificationWithCloseTimeout(
        @NotNull String titleI18nKey,
        @NotNull String subtitleI18nKey,
        @NotNull String contentI18nKey,
        @NotNull NotificationType messageLevel,
        int timeoutMilliseconds
    );

    void showNotification(
        @NotNull String titleI18nKey,
        @NotNull String subtitleI18nKey,
        @NotNull String contentI18nKey,
        @NotNull NotificationType messageLevel,
        @NotNull NotificationListener listener
    );

    void showNotificationWithCloseTimeout(
        @NotNull String titleI18nKey,
        @NotNull String subtitleI18nKey,
        @NotNull String contentI18nKey,
        @NotNull NotificationType messageLevel,
        int timeoutMilliseconds,
        @NotNull NotificationListener listener
    );

    void showImportantNotification(
        @NotNull String titleI18nKey,
        @NotNull String subtitleI18nKey,
        @NotNull String contentI18nKey,
        @NotNull NotificationType messageLevel,
        @NotNull NotificationListener listener
    );

    void showImportantNotificationWithCloseTimeout(
        @NotNull String titleI18nKey,
        @NotNull String subtitleI18nKey,
        @NotNull String contentI18nKey,
        @NotNull NotificationType messageLevel,
        int timeoutMilliseconds,
        @NotNull NotificationListener listener
    );
}
