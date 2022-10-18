import { Component } from '@angular/core';
import { NzMessageService, UploadFile } from 'ng-zorro-antd';
import { HttpClient, HttpHeaders, HttpParams, HttpRequest, HttpResponse } from '@angular/common/http';
import { filter } from 'rxjs/operators';
import { AsyncSubject, Observable } from 'rxjs';


export interface SelectedFiles {
  name: string;
  file: any;
  base64?: string;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'fileToBase64Angular';

  uploading = false;
  fileList: UploadFile[] = [];
  disabled = false;

  //Tamaño en bytes
  public chunkSize = 10*100000; //1 byte -> 1kb = 1000 byte -> 1mb = 1000kb

  constructor(private http: HttpClient, private msg: NzMessageService) {
  }


  public selectedFiles: SelectedFiles[] = [];

  public toFilesBase64(files: File[], selectedFiles: SelectedFiles[]): Observable<SelectedFiles[]> {
    const result = new AsyncSubject<SelectedFiles[]>();
    if (files?.length) {
      Object.keys(files)?.forEach(async (file, i) => {
        const reader = new FileReader();
        reader.onload = (e) => {
          selectedFiles = selectedFiles?.filter(f => f?.name !== files[i]?.name);
          selectedFiles.push({ name: files[i]?.name, file: files[i], base64: reader?.result as string });
          result.next(selectedFiles);
          if (files?.length === (i + 1)) {
            result.complete();
          }
        };
        reader.readAsDataURL(files[i]);
      });
      return result;
    } else {
      result.next([]);
      result.complete();
      return result;
    }
  }

  public onFileSelected(files: File[]) {
    // this.selectedFiles = []; // clear
    this.toFilesBase64(files, this.selectedFiles).subscribe((res: SelectedFiles[]) => {
      this.selectedFiles = res;
    });
  }


  beforeUpload = (file: UploadFile): boolean => {
    this.fileList = this.fileList.concat(file);
    return false;
  };

  handleUpload(): void {
    const formData = new FormData();
    // tslint:disable-next-line:no-any
    this.fileList.forEach((file: any) => {
      formData.append('files[]', file);
      const result = new AsyncSubject<SelectedFiles[]>();
      let selectedFiles: SelectedFiles[] = [];


      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = (e) => {
        selectedFiles = selectedFiles?.filter(f => f?.name !== file?.name);
        selectedFiles.push({ name: file?.name, file, base64: reader?.result as string });
        result.next(selectedFiles);
        // if (files?.length === (i + 1)) {
        //   result.complete();
        // }

        this.uploading = true;
        // // You can use any AJAX library you like
        // const req = new HttpRequest('POST', '/api/carga', {
        //   base64: selectedFiles[0].base64
        // });

        const req = new HttpRequest('POST', '/api/encode', {
          base64: selectedFiles[0].base64
        });

        this.http
          .request(req)
          .pipe(filter(e => e instanceof HttpResponse))
          .subscribe(
            () => {
              this.uploading = false;
              this.fileList = [];
              this.msg.success('upload successfully.');
            },
            () => {
              this.uploading = false;
              this.msg.error('upload failed.');
            }
          );
      };
    });
  }


  public chunkedFiles(): void {
    this.fileList.forEach((file: UploadFile) => {
      let currentChunkIndex = 0;
      const totalChunk = Math.ceil(file.size / this.chunkSize);
      for (var i = 0; i < totalChunk; i++) {
        const reader = new FileReader();
        let from = currentChunkIndex * this.chunkSize;
        let to = from + this.chunkSize;
        let blob = file.slice(from, to);
        reader.onload = e => this.uploadChunk(e, (currentChunkIndex + ""));
        reader.readAsDataURL(blob);
        ++currentChunkIndex;
        // reader.readAsDataURL(file.slice(0, file.size));
      }
    });
  }

  public uploadChunk(readerEvent: ProgressEvent<FileReader>, index: string): void {
    const data = readerEvent.target.result;
    const file = this.fileList[0];
    const params = new HttpParams()
      .append('name', file.name)
      .append('size', file.size.toString())
      .append('currentChunkIndex', index)
      .append('totalChunks', Math.ceil(file.size / this.chunkSize).toString());

    const headers = new HttpHeaders();
    headers.append('Content-Type', 'application/octet-stream');

    const req = new HttpRequest('POST', '/api/stream?' + params.toString(), { data }, { headers });
    // const req = new HttpRequest('POST', '/api/stream', {data}, {headers});

    this.http
      .request(req)
      .pipe(filter(e => e instanceof HttpResponse))
      .subscribe(
        () => {
          this.uploading = false;
          this.fileList = [];
          this.msg.success('upload successfully.');
        },
        () => {
          this.uploading = false;
          this.msg.error('upload failed.');
        }
      );



  }

}
