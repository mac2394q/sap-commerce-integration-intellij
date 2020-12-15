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

package com.intellij.idea.plugin.hybris.common.services.impl;

import com.intellij.idea.plugin.hybris.common.services.NotificationService;
import com.intellij.idea.plugin.hybris.common.utils.HybrisI18NBundleUtils;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.util.Alarm;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Vlad Bozhenok <vladbozhenok@gmail.com>
 */
public class DefaultNotificationService implements NotificationService {

    private final NotificationGroup group;
    private final Project project;
    private final Alarm notificationClosingAlarm;

    public DefaultNotificationService(@NotNull final NotificationGroup group, @NotNull final Project project) {
        this.group = group;
        this.project = project;
        this.notificationClosingAlarm = new Alarm(project);
    }

    @Override
    public void showNotification(
        @NotNull final String contentI18nKey,
        @NotNull final NotificationType messageLevel
    ) {
        this.showNotificationInner(null, null, contentI18nKey, messageLevel, null);
    }

    @Override
    public void showNotificationWithCloseTimeout(
        @NotNull final String contentI18nKey,
        @NotNull final NotificationType messageLevel,
        final int timeoutMilliseconds
    ) {
        this.showNotificationWithCloseTimeoutInner(
            null, null, contentI18nKey, messageLevel, timeoutMilliseconds, null
        );
    }

    @Override
    public void showImportantNotification(
        @NotNull final String contentI18nKey,
        @NotNull final NotificationType messageLevel
    ) {
        this.showImportantNotificationInner(null, null, contentI18nKey, messageLevel, null);
    }

    @Override
    public void showImportantNotificationWithCloseTimeout(
        @NotNull final String contentI18nKey,
        @NotNull final NotificationType messageLevel,
        final int timeoutMilliseconds
    ) {
        this.showImportantNotificationWithCloseTimeoutInner(
            null, null, contentI18nKey, messageLevel, timeoutMilliseconds, null
        );
    }

    @Override
    public void showNotification(
        @NotNull final String contentI18nKey,
        @NotNull final NotificationType messageLevel,
        @NotNull final NotificationListener listener
    ) {
        this.showNotificationInner(null, null, contentI18nKey, messageLevel, listener);
    }

    @Override
    public void showNotificationWithCloseTimeout(
        @NotNull final String contentI18nKey,
        @NotNull final NotificationType messageLevel,
        final int timeoutMilliseconds,
        @NotNull final NotificationListener listener
    ) {
        this.showNotificationWithCloseTimeoutInner(
            null, null, contentI18nKey, messageLevel, timeoutMilliseconds, listener
        );
    }

    @Override
    public void showImportantNotification(
        @NotNull final String contentI18nKey,
        @NotNull final NotificationType messageLevel,
        @NotNull final NotificationListener listener
    ) {
        this.showImportantNotificationInner(null, null, contentI18nKey, messageLevel, listener);
    }

    @Override
    public void showImportantNotificationWithCloseTimeout(
        @NotNull final String contentI18nKey,
        @NotNull final NotificationType messageLevel,
        final int timeoutMilliseconds,
        @NotNull final NotificationListener listener
    ) {
        this.showImportantNotificationWithCloseTimeoutInner(
            null, null, contentI18nKey, messageLevel, timeoutMilliseconds, listener
        );
    }

    @Override
    public void showNotification(
        @NotNull final String titleI18nKey,
        @NotNull final String contentI18nKey,
        @NotNull final NotificationType messageLevel
    ) {
        this.showNotificationInner(titleI18nKey, null, contentI18nKey, messageLevel, null);
    }

    @Override
    public void showNotificationWithCloseTimeout(
        @NotNull final String titleI18nKey,
        @NotNull final String contentI18nKey,
        final int timeoutMilliseconds,
        @NotNull final NotificationType messageLevel
    ) {
        this.showNotificationWithCloseTimeoutInner(
            titleI18nKey, null, contentI18nKey, messageLevel, timeoutMilliseconds, null
        );
    }

    @Override
    public void showImportantNotification(
        @NotNull final String titleI18nKey,
        @NotNull final String contentI18nKey,
        @NotNull final NotificationType messageLevel
    ) {
        this.showImportantNotificationInner(titleI18nKey, null, contentI18nKey, messageLevel, null);
    }

    @Override
    public void showImportantNotificationWithCloseTimeout(
        @NotNull final String titleI18nKey,
        @NotNull final String contentI18nKey,
        @NotNull final NotificationType messageLevel,
        final int timeoutMilliseconds
    ) {
        this.showImportantNotificationWithCloseTimeoutInner(
            titleI18nKey, null, contentI18nKey, messageLevel, timeoutMilliseconds, null
        );
    }

    @Override
    public void showNotification(
        @NotNull final String titleI18nKey,
        @NotNull final String contentI18nKey,
        @NotNull final NotificationType messageLevel,
        @NotNull final NotificationListener listener
    ) {
        this.showNotificationInner(titleI18nKey, null, contentI18nKey, messageLevel, listener);
    }

    @Override
    public void showNotificationWithCloseTimeout(
        @NotNull final String titleI18nKey,
        @NotNull final String contentI18nKey,
        @NotNull final NotificationType messageLevel,
        final int timeoutMilliseconds,
        @NotNull final NotificationListener listener
    ) {
        this.showNotificationWithCloseTimeoutInner(
            titleI18nKey, null, contentI18nKey, messageLevel, timeoutMilliseconds, listener
        );
    }

    @Override
    public void showImportantNotification(
        @NotNull final String titleI18nKey,
        @NotNull final String contentI18nKey,
        @NotNull final NotificationType messageLevel,
        @NotNull final NotificationListener listener
    ) {
        this.showImportantNotificationInner(titleI18nKey, null, contentI18nKey, messageLevel, listener);
    }

    @Override
    public void showImportantNotificationWithCloseTimeout(
        @NotNull final String titleI18nKey,
        @NotNull final String contentI18nKey,
        @NotNull final NotificationType messageLevel,
        final int timeoutMilliseconds,
        @NotNull final NotificationListener listener
    ) {
        this.showImportantNotificationWithCloseTimeoutInner(
            titleI18nKey, null, contentI18nKey, messageLevel, timeoutMilliseconds, listener
        );
    }

    @Override
    public void showNotification(
        @NotNull final String titleI18nKey,
        @NotNull final String subtitleI18nKey,
        @NotNull final String contentI18nKey,
        @NotNull final NotificationType messageLevel
    ) {
        this.showNotificationInner(titleI18nKey, subtitleI18nKey, contentI18nKey, messageLevel, null);
    }

    @Override
    public void showNotificationWithCloseTimeout(
        @NotNull final String titleI18nKey,
        @NotNull final String subtitleI18nKey,
        @NotNull final String contentI18nKey,
        @NotNull final NotificationType messageLevel,
        final int timeoutMilliseconds
    ) {
        this.showNotificationWithCloseTimeoutInner(
            titleI18nKey, subtitleI18nKey, contentI18nKey, messageLevel, timeoutMilliseconds, null
        );
    }

    @Override
    public void showImportantNotification(
        @NotNull final String titleI18nKey,
        @NotNull final String subtitleI18nKey,
        @NotNull final String contentI18nKey,
        @NotNull final NotificationType messageLevel
    ) {
        this.showImportantNotificationInner(titleI18nKey, subtitleI18nKey, contentI18nKey, messageLevel, null);
    }

    @Override
    public void showImportantNotificationWithCloseTimeout(
        @NotNull final String titleI18nKey,
        @NotNull final String subtitleI18nKey,
        @NotNull final String contentI18nKey,
        @NotNull final NotificationType messageLevel,
        final int timeoutMilliseconds
    ) {
        this.showImportantNotificationWithCloseTimeoutInner(
            titleI18nKey, subtitleI18nKey, contentI18nKey, messageLevel, timeoutMilliseconds, null
        );
    }

    @Override
    public void showNotification(
        @NotNull final String titleI18nKey,
        @NotNull final String subtitleI18nKey,
        @NotNull final String contentI18nKey,
        @NotNull final NotificationType messageLevel,
        @NotNull final NotificationListener listener
    ) {
        this.showNotificationInner(titleI18nKey, subtitleI18nKey, contentI18nKey, messageLevel, listener);
    }

    @Override
    public void showNotificationWithCloseTimeout(
        @NotNull final String titleI18nKey,
        @NotNull final String subtitleI18nKey,
        @NotNull final String contentI18nKey,
        @NotNull final NotificationType messageLevel,
        final int timeoutMilliseconds,
        @NotNull final NotificationListener listener
    ) {
        this.showNotificationWithCloseTimeoutInner(
            titleI18nKey, subtitleI18nKey, contentI18nKey, messageLevel, timeoutMilliseconds, listener
        );
    }

    @Override
    public void showImportantNotification(
        @NotNull final String titleI18nKey,
        @NotNull final String subtitleI18nKey,
        @NotNull final String contentI18nKey,
        @NotNull final NotificationType messageLevel,
        @NotNull final NotificationListener listener
    ) {
        this.showImportantNotificationInner(titleI18nKey, subtitleI18nKey, contentI18nKey, messageLevel, listener);
    }

    @Override
    public void showImportantNotificationWithCloseTimeout(
        @NotNull final String titleI18nKey,
        @NotNull final String subtitleI18nKey,
        @NotNull final String contentI18nKey,
        @NotNull final NotificationType messageLevel,
        final int timeoutMilliseconds,
        @NotNull final NotificationListener listener
    ) {
        this.showImportantNotificationWithCloseTimeoutInner(
            titleI18nKey, subtitleI18nKey, contentI18nKey, messageLevel, timeoutMilliseconds, listener
        );
    }

    @NotNull
    private Notification showNotificationInner(
        @Nullable final String titleI18nKey,
        @Nullable final String subtitleI18nKey,
        @Nullable final String contentI18nKey,
        @NotNull final NotificationType messageLevel,
        @Nullable final NotificationListener listener
    ) {
        final Notification notification = buildNotification(
            titleI18nKey, subtitleI18nKey, contentI18nKey, messageLevel, listener
        );

        Notifications.Bus.notify(notification, project);

        return notification;
    }

    private void showNotificationWithCloseTimeoutInner(
        @Nullable final String titleI18nKey,
        @Nullable final String subtitleI18nKey,
        @Nullable final String contentI18nKey,
        @NotNull final NotificationType messageLevel,
        final int timeoutMilliseconds,
        @Nullable final NotificationListener listener
    ) {
        final Notification notification = this.showNotificationInner(
            titleI18nKey, subtitleI18nKey, contentI18nKey, messageLevel, listener
        );

        this.registerNotificationCloseTimeout(notification, timeoutMilliseconds);
    }

    private Notification showImportantNotificationInner(
        @Nullable final String titleI18nKey,
        @Nullable final String subtitleI18nKey,
        @Nullable final String contentI18nKey,
        @NotNull final NotificationType messageLevel,
        @Nullable final NotificationListener listener
    ) {
        final Notification notification = buildNotification(
            titleI18nKey, subtitleI18nKey, contentI18nKey, messageLevel, listener
        );

        notification.setImportant(true);

        Notifications.Bus.notify(notification, project);

        return notification;
    }

    private void showImportantNotificationWithCloseTimeoutInner(
        @Nullable final String titleI18nKey,
        @Nullable final String subtitleI18nKey,
        @Nullable final String contentI18nKey,
        @NotNull final NotificationType messageLevel,
        final int timeoutMilliseconds,
        @Nullable final NotificationListener listener
    ) {
        final Notification notification = this.showImportantNotificationInner(
            titleI18nKey, subtitleI18nKey, contentI18nKey, messageLevel, listener
        );

        this.registerNotificationCloseTimeout(notification, timeoutMilliseconds);
    }

    @NotNull
    private Notification buildNotification(
        @Nullable final String titleI18nKey,
        @Nullable final String subtitleI18nKey,
        @Nullable final String contentI18nKey,
        @NotNull final NotificationType messageLevel,
        @Nullable final NotificationListener listener
    ) {
        final String title = (null == titleI18nKey) ? "" : HybrisI18NBundleUtils.message(titleI18nKey);
        final String subtitle = (null == subtitleI18nKey) ? "" : HybrisI18NBundleUtils.message(subtitleI18nKey);
        final String content = (null == contentI18nKey) ? "" : HybrisI18NBundleUtils.message(contentI18nKey);

        return this.group.createNotification(title, subtitle, content, messageLevel, listener);
    }

    private void registerNotificationCloseTimeout(
        @Nonnull final Notification notification,
        final int timeoutMilliseconds
    ) {
        if (timeoutMilliseconds > 0) {
            ApplicationManager.getApplication().invokeLater(() -> {
                if (!this.notificationClosingAlarm.isDisposed()) {
                    this.notificationClosingAlarm.addRequest(notification::hideBalloon, timeoutMilliseconds);
                }
            });
        }
    }
}
