package applicationInfo.models

import domain.models.info.AppInfoDetail


//Mapped from AppInfo Domain class to a simpler key, value pair to be displayed in the UI
// Key describes the info item such as Java info, value holds the actual Java info
typealias ApplicationInfoForUi = Map<String, List<AppInfoDetail>>