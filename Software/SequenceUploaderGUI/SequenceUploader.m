% SequenceUploader.m
% 
% GUI front-end to handle uploading laser sequences to Arduino via USB
% serial.
% Requires Arduino to be running corresponding SerialEvent.ino sketch 
% 

% Changed data type to cell.  Need to update load, save, functions
% to reflect this.

function SequenceUploader(varargin)

    if nargin == 1;
        % Provide com port as string
        comPort = varargin{1};
    else
        comPort = 'COM3';
    end
    %

    s1 = 1;
% 
%     % Initialize serial connection to Arduino
%     % Assuming COM3 here.  This may change on other systems and needs
%     % to be updated here.
    s1 = serial(comPort, 'BaudRate', 9600, 'Timeout', 5);
    fopen(s1);
    pause(2); % Wait for Arduino to restart

    % Send something to test connection
    fprintf(s1, 'E\n');
    rcd = fscanf(s1);
%     disp(uint8(rcd));
    serOut = receiveSerial(s1);
%     disp(serOut);

    if strcmp(rcd, char([69 99 104 111 32 83 101 113 117 101 110 99 101 13 10])); % Receive 'Echo Sequence'
        openGUI = findobj('Tag', 'GUIFig');
        if isempty(openGUI)
            initGUI;
        else
            figure(openGUI);
        end
    else
        delete(s1);
        errordlg(sprintf('%s\n%s%s', ...
            'Serial connection error', 'Ensure Arduino is connected to ', comPort), 'Serial error');

    end

    guiObj = findobj('Tag', 'GUIFig');
    if ~isempty(guiObj)
        handles = guidata(findobj('Tag', 'GUIFig'));
        handles.comPort = comPort;
        handles.serObj = s1;
        guidata(handles.guiFig, handles);
    end
    
end
        
function initGUI(varargin)

    % Figure
    handles.guiFig = figure();
    figPost = get(handles.guiFig, 'Position');
    set(handles.guiFig, 'Position', [figPost(1), figPost(2), 676, 400], ...
        'toolbar', 'figure', 'menubar', 'none', 'name', 'Laser Sequence Uploader', ...
        'numbertitle', 'off', 'DeleteFcn', @StopSerial, 'Tag', 'GUIFig');


    % Buttons
    handles.Load = uicontrol('Style', 'pushbutton', 'Parent', handles.guiFig, ...
        'Position', [547 345 125 50], ...
        'String', 'Load', 'FontSize', 12, 'Callback', @LoadSequence);

    handles.Save = uicontrol('Style', 'pushbutton', 'Parent', handles.guiFig, ...
        'Position', [547 290 125 50], ...
        'String', 'Save', 'FontSize', 12, 'Callback', @SaveSequence);

    handles.SetCounter = uicontrol('Style', 'pushbutton', 'Parent', handles.guiFig, ...
        'Position', [547 235 125 50], ...
        'String', 'Reset Count', 'FontSize', 12, 'Callback', @ResetCounter);

    handles.Upload = uicontrol('Style', 'pushbutton', 'Parent', handles.guiFig, ...
        'Position', [547 15 125 50], ...
        'String', 'Upload', 'FontSize', 12, 'Callback', @UploadSequence);

    handles.AllOn = uicontrol('Style', 'pushbutton', 'Parent', handles.guiFig, ...
        'Position', [547 125 125 50], ...
        'String', 'All On', 'FontSize', 12, 'Callback', @AllOnFcn);

    handles.TestSeq  = uicontrol('Style', 'pushbutton', 'Parent', handles.guiFig, ...
        'Position', [547 70 125 50], ...
        'String', 'Test Sequence', 'FontSize', 12, 'Callback', @TestSequence);

    handles.ClearSeq  = uicontrol('Style', 'pushbutton', 'Parent', handles.guiFig, ...
        'Position', [547 180 125 50], ...
        'String', 'Clear Sequence', 'FontSize', 12, 'Callback', @ClearSequence);
    
    
    % Table
    handles.seqArray = cell(64,6);
    handles.seqArray(:) = {false};
    handles.seqArray = [handles.seqArray, cell(64, 1)];
    handles.seqArray(:,7) = {0};
    handles.seqArray(1,1:6) = {true};
    handles.seqArray(1,7) = {1};

    
    colWidth = 67;
    colFontSize = 5;
    handles.SequenceTable = uitable(handles.guiFig, 'Data', handles.seqArray, ...
        'ColumnName', {colText('Chan 1', '#A2BE2', colFontSize), ...
                       colText('Chan 2', '#1E90FF', colFontSize),...
                       colText('Chan 3', 'lime', colFontSize),...
                       colText('Chan 4', 'orange', colFontSize),...
                       colText('Chan 5', '#DC143C', colFontSize),...
                       colText('Chan 6', '#555555', colFontSize), ...
                       colText('Count', 'black', colFontSize)}, ...
        'ColumnWidth', {colWidth, colWidth, colWidth, colWidth, colWidth, colWidth, colWidth+10}, ...
        'ColumnFormat', {'logical', 'logical', 'logical', 'logical', 'logical', 'logical', 'numeric'}, ...
        'FontSize', 16, 'ColumnEditable', true(1,7), 'CellEditCallback', @TableCallback);
    handles.SequenceTable.Position = [10 10 531 380];
    handles.SequenceTable.BackgroundColor = [1 1 1; ...
                                             .95 .95 .95];
                                             
                              
    guidata(handles.guiFig, handles);
    
	function outHtml = colText(inText, inColor, inSize)
        % return a HTML string with colored font of defined size
        outHtml = ['<html><font color="', ...
              inColor, ...
              '" size="', ...
              num2str(inSize), ...
              '">', ...
              inText, ...
              '</font></html>'];
	end

end

function LoadSequence(varargin)

handles = guidata(findobj('Tag', 'GUIFig'));

[fileName, pathName, ~] = uigetfile({'*.lsq', 'Sequence File (*.lsq)'},...
    'Load Sequence');

    if ~(isequal(fileName,0) || isequal(pathName,0))
       % Read in LSQ sequence (tab-delimited text) file
       
       readIn = dlmread(fullfile(pathName, fileName), '\t', 1, 0);
       dataIn = logical(readIn(:,1:6));
       handles.SequenceTable.Data(1:size(dataIn, 1), 1:6) = num2cell(dataIn);
       handles.SequenceTable.Data(1:size(dataIn, 1), 7) = num2cell(readIn(:,7));
       
       handles.seqArray(1:size(dataIn, 1), :) = num2cell(readIn);
       
       set(handles.guiFig, 'Name', strcat('Laser Sequence Updater', ' - ', fileName));
        
    end
    
end

function SaveSequence(varargin)

handles = guidata(findobj('Tag', 'GUIFig'));

[fileName, pathName, ~] = uiputfile({'*.lsq', 'Sequence File (*.lsq)'},...
    'Save Sequence');

    if ~(isequal(fileName,0) || isequal(pathName,0))
        % Save file to specified location as tab-delimited text file
       
        % Get data from table
        % Only those rows between first any(true) and last any(true) are
        % used
        
%         dataHere = handles.SequenceTable.Data;
%         firstRow = find(any(dataHere < 0, 2), 1, 'first');
%         lastRow = find(any(dataHere < 0, 2), 1, 'last');
        
        dataHere = cell2mat(handles.SequenceTable.Data(:,1:6));
        loopCount = cell2mat(handles.SequenceTable.Data(:,7));
        firstRow = find(any(dataHere, 2), 1, 'first');
        lastRow = find(any(dataHere, 2), 1, 'last');

        dataHere = ((dataHere(firstRow:lastRow,1:6)));
        countHere = (loopCount(firstRow:lastRow,1));
        
        dataHere = [dataHere, countHere];
                
        fID = fopen(fullfile(pathName, fileName), 'w');
        fprintf(fID, '# Laser Sequence\r\n');
        for k = 1:size(dataHere, 1);
            fprintf(fID, '%d\t%d\t%d\t%d\t%d\t%d\t%d\r\n', dataHere(k,:));
        end
        fclose(fID);
        
        set(handles.guiFig, 'Name', strcat('Laser Sequence Updater', ' - ', fileName));
        
    end

end

function serOut = receiveSerial(serObj)

    serOut = '';
    rcd = fscanf(serObj);
    
    if isempty(rcd)
        return
    else
        serOut = strcat(serOut, rcd);
    end

end

function ResetCounter(varargin)

    handles = guidata(findobj('Tag', 'GUIFig'));
    fprintf(handles.serObj, 'R\n');

end

function UploadSequence(varargin)

    handles = guidata(findobj('Tag', 'GUIFig'));
    
    fprintf(1, 'Uploading sequence...\n');
    
    dataHere = cell2mat(handles.SequenceTable.Data(:,1:6));
    loopCount = cell2mat(handles.SequenceTable.Data(:,7));
    firstRow = find((any(dataHere, 2) | loopCount > 0), 1, 'first');
    lastRow = find((any(dataHere, 2) | loopCount > 0), 1, 'last');
    
    dataHere = fliplr(uint8((dataHere(firstRow:lastRow,1:6)))); % Necessary with shield as C1-C6 labels are transposed
    countHere = (loopCount(firstRow:lastRow,1));
    
    handles.seqArray(firstRow:lastRow, :) = num2cell([dataHere, countHere]);
    
    fprintf(handles.serObj, 'C\n');

    for k = 1:size(dataHere, 1);
%         fprintf(handles.serObj, char([65 32]));
        
        charMat = repmat(uint8(48), 1, 6);
        %  'A BXXXXXX\n'
        charMat = [65 32 66 charMat + (dataHere(k,:)) 10];
        fprintf(handles.serObj, char(charMat));
        
%         stringToUpload = sprintf('B%s%s%s%s%s%s', num2str(dataHere(k,:)));
%         stringToUpload = strrep(stringToUpload, ' ', '');
%         fprintf(handles.serObj, stringToUpload);
%         fprintf(handles.serObj, char(10));
        pause(0.1);
        serOut = receiveSerial(handles.serObj);
%         disp(serOut);
    end
    
    % Upload loop count values
    % 'N X,X,X,X,X,\n'
    fmtStr = repmat('%u,', [1 numel(countHere)]);
    loopList = sprintf(fmtStr, (countHere));
    charMat = [78 32 loopList 10];
    fprintf(handles.serObj, char(charMat));
    pause(0.1);
    serOut = receiveSerial(handles.serObj);
%     disp(serOut);
    
%     pause(0.5);
    
    serOut = receiveSerial(handles.serObj);
%     disp(serOut);

    % Confirm upload
    fprintf(1, 'Confirming upload...\n');
    confd = ConfirmUpload(num2cell([dataHere, countHere]));
    
    if confd
        fprintf(1, 'Upload Confirmed!\n');
    else
        fprintf(1, 'Upload unsucessful. Please retry.\n');
    end

end

function confd = ConfirmUpload(dataCheck)

    handles = guidata(findobj('Tag', 'GUIFig'));
    flushinput(handles.serObj);
    fprintf(handles.serObj, 'E\n');
    pause(0.1);
    receiveSerial(handles.serObj);
    serLeft = 1;
    handles.serObj.BytesAvailable();
    serData = '';
    while serLeft == 1;
        
        if handles.serObj.BytesAvailable() == 0
            serLeft = 0;
        else
            serIn = receiveSerial(handles.serObj);
            serData = [serData ' ' serIn];
        end
    end
    % Format received and check against stored table values
    serData(1) = [];
    xPosts = strfind(serData, ' ');
    serData(xPosts(2:2:end)) = ':';
    serData = strsplit(serData, ':')';
    for k = 1:length(serData)
        serData{k} = strsplit(serData{k},  ' ');
    end
    
    holdCell = cell(size(serData, 1), 7);
    for k = 1:size(serData, 1)
        
        holdCell(k, 1:6) = num2cell(strcmp(cellstr(dec2bin(hex2dec(serData{k}{1}(3:end)), 6)')', '1'));
        holdCell(k, 7) = {str2double(serData{k}(2))};
        
    end
      
    confd = isequal(dataCheck, holdCell);

end

function AllOnFcn(varargin)

    handles = guidata(findobj('Tag', 'GUIFig'));
    
    handles.SequenceTable.Data(:,1:6) = num2cell(false(64, 6));
    handles.SequenceTable.Data(:,7) = num2cell(zeros(64, 1));
    handles.SequenceTable.Data(1,1:6) = num2cell(logical([1 1 1 1 1 1]));
    handles.SequenceTable.Data(1,7) = {1};
    
    handles.seqArray = handles.SequenceTable.Data;
    
    fprintf(handles.serObj, 'F\n');
    
    set(handles.guiFig, 'Name', 'Laser Sequence Updater');
    
    serOut = receiveSerial(handles.serObj);
    disp(serOut);

end

function TestSequence(varargin)

    handles = guidata(findobj('Tag', 'GUIFig'));
    fprintf(handles.serObj, 'T\n');
    
    serOut = receiveSerial(handles.serObj);
    disp(serOut);

end

function ClearSequence(varargin)

    handles = guidata(findobj('Tag', 'GUIFig'));
    handles.SequenceTable.Data(:,1:6) = num2cell(false(64, 6));
    handles.SequenceTable.Data(:,7) = num2cell(zeros(64, 1));
    fprintf(handles.serObj, 'C\n');
    
    set(handles.guiFig, 'Name', 'Laser Sequence Updater');
    
    serOut = receiveSerial(handles.serObj);
    disp(serOut);
    
end

function TableCallback(tabHand, editData)

    if isa(editData.EditData, 'logical')
    
        if editData.EditData;
            tabHand.Data(editData.Indices(1), editData.Indices(2)) = {true};
        elseif ~editData.EditData
            tabHand.Data(editData.Indices(1), editData.Indices(2)) = {false};
        end
        

    elseif isa(editData.NewData, 'numeric');
        
        oldVal = editData.NewData;
        if (oldVal >= 0) && (oldVal < ((2^32)-1))
            % OK value
            
            tabHand.Data(editData.Indices(1), editData.Indices(2)) = {round(editData.NewData)};
            
        else
            tabHand.Data(editData.Indices(1), editData.Indices(2)) = {editData.PreviousData};
        end
    end

end

function StopSerial(varargin)

handles = guidata(findobj('Tag', 'GUIFig'));
disp('Halting Serial Connection')
fclose(handles.serObj);
delete(handles.serObj);


end
    

