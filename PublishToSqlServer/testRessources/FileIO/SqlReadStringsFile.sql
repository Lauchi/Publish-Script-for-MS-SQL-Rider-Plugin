CREATE PROCEDURE [dbo].[GetTaskRedirectionByAutomaticTaskId]
	@AutomaticTaskId nvarchar(100)
AS
	SELECT Id
	FROM [dbo].[AutogeneratedTaskRedirections]
	WHERE AutomaticTaskId = @AutomaticTaskId
	AND ISNULL([IsDeleted], 1) = 0
RETURN 0
