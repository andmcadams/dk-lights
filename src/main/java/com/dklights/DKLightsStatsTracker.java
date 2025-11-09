package com.dklights;

import java.time.Duration;
import java.time.Instant;
import java.util.regex.Matcher;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;

@Slf4j
public class DKLightsStatsTracker
{

	@Getter
	private int lampsFixed = 0;
	@Getter
	private int lampsPerHr = 0;
	@Getter
	private int totalLampsFixed = 0;

	private Instant start;

	public DKLightsStatsTracker()
	{
	}

	public void incrementLampsFixed()
	{
		++lampsFixed;

		if (start == null)
		{
			start = Instant.now();
		}

		Duration elapsed = Duration.between(start, Instant.now());
		long elapsedMs = elapsed.toMillis();

		if (lampsFixed >= 3 && elapsedMs > 0)
		{
			lampsPerHr = (int) ((double) lampsFixed * Duration.ofHours(1).toMillis() / elapsedMs);
		}
	}

	public void onChatMessage(ChatMessage chatMessage)
	{
		ChatMessageType chatMessageType = chatMessage.getType();
		String message = chatMessage.getMessage();

		if (chatMessageType == ChatMessageType.SPAM
				&& message.contains(DKLightsConstants.TOTAL_LAMPS_FIXED_CHAT_MESSAGE))
		{
			updateTotalLampsFixed(message);
		}
	}

	public void updateTotalLampsFixed(String message)
	{
		Matcher matcher = DKLightsConstants.TOTAL_LAMPS_PATTERN.matcher(message);

		if (matcher.find())
		{
			try
			{
				String numberStr = matcher.group(1).replace(",", "");
				int number = Integer.parseInt(numberStr);

				if (number > 0 && number > this.totalLampsFixed)
				{
					this.totalLampsFixed = number;
				}
			}
			catch (NumberFormatException e)
			{
				log.warn("Failed to parse number from chat message: {}", matcher.group(1));
			}
		}
	}

	public void resetSession()
	{
		lampsFixed = 0;
		lampsPerHr = 0;
		start = null;
	}
}
